/**
 * build-file-parser
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.buildfileparser.parser;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;

import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class DependenciesVisitor extends CodeVisitorSupport {
    private final ExternalIdFactory externalIdFactory;
    private final List<Dependency> dependencies = new ArrayList<>();

    private boolean inDependenciesBlock;

    public DependenciesVisitor(final ExternalIdFactory externalIdFactory) {
        super();
        this.externalIdFactory = externalIdFactory;
    }

    @Override
    public void visitMethodCallExpression(final MethodCallExpression methodCallExpression) {
        if ("dependencies".equals(methodCallExpression.getMethodAsString())) {
            inDependenciesBlock = true;
        } else {
            inDependenciesBlock = false;
        }

        super.visitMethodCallExpression(methodCallExpression);
    }

    @Override
    public void visitArgumentlistExpression(final ArgumentListExpression argumentListExpression) {
        if (inDependenciesBlock) {
            final List<Expression> expressions = argumentListExpression.getExpressions();

            if (expressions.size() == 1 && expressions.get(0) instanceof ClosureExpression) {
                final ClosureExpression closureExpression = (ClosureExpression) expressions.get(0);
                if (closureExpression.getCode() instanceof BlockStatement) {
                    final BlockStatement blockStatement = (BlockStatement) closureExpression.getCode();
                    final List<Statement> statements = blockStatement.getStatements();
                    for (final Statement statement : statements) {
                        if (statement instanceof ExpressionStatement) {
                            final ExpressionStatement expressionStatement = (ExpressionStatement) statement;
                            final Expression expression = expressionStatement.getExpression();
                            addDependencyFromExpression(expression);
                        } else if (statement instanceof ReturnStatement) {
                            final ReturnStatement returnStatement = (ReturnStatement) statement;
                            final Expression expression = returnStatement.getExpression();
                            addDependencyFromExpression(expression);
                        }
                    }
                }
            } else if (expressions.size() == 1 && expressions.get(0) instanceof ConstantExpression) {
                final ConstantExpression constantExpression = (ConstantExpression) expressions.get(0);
                addDependencyFromConstantExpression(constantExpression);
            }
        }

        super.visitArgumentlistExpression(argumentListExpression);
    }

    private void addDependencyFromExpression(final Expression expression) {
        if (expression instanceof MethodCallExpression) {
            final MethodCallExpression methodCallExpression = (MethodCallExpression) expression;
            final Expression argumentsExpression = methodCallExpression.getArguments();
            if (argumentsExpression instanceof ArgumentListExpression) {
                final ArgumentListExpression methodArgumentListExpression = (ArgumentListExpression) argumentsExpression;
                final List<Expression> methodExpressions = methodArgumentListExpression.getExpressions();
                if (methodExpressions.size() == 1 && methodExpressions.get(0) instanceof ConstantExpression) {
                    final ConstantExpression methodConstantExpression = (ConstantExpression) methodExpressions.get(0);
                    addDependencyFromConstantExpression(methodConstantExpression);
                }
            }
        }
    }

    @Override
    public void visitMapExpression(final MapExpression mapExpression) {
        if (inDependenciesBlock) {
            final List<MapEntryExpression> mapEntryExpressions = mapExpression.getMapEntryExpressions();

            String group = null;
            String name = null;
            String version = null;
            for (final MapEntryExpression mapEntryExpression : mapEntryExpressions) {
                final String key = mapEntryExpression.getKeyExpression().getText();
                final String value = mapEntryExpression.getValueExpression().getText();
                if ("group".equals(key)) {
                    group = value;
                } else if ("name".equals(key)) {
                    name = value;
                } else if ("version".equals(key)) {
                    version = value;
                }
            }

            addDependency(group, name, version);
        }

        super.visitMapExpression(mapExpression);
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    private void addDependency(final String group, final String name, final String version) {
        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        final Dependency dependency = new Dependency(name, version, externalId);
        dependencies.add(dependency);
        System.out.println(dependency.externalId.createExternalId());
    }

    private void addDependencyFromConstantExpression(final ConstantExpression constantExpression) {
        final String dependencyString = constantExpression.getText();
        final String[] pieces = dependencyString.split(":");

        if (pieces.length == 3) {
            final String group = pieces[0];
            final String name = pieces[1];
            final String version = pieces[2];
            addDependency(group, name, version);
        }
    }

}
