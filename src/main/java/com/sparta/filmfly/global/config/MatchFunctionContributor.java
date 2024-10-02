package com.sparta.filmfly.global.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

public class MatchFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        BasicType<Double> resultType = functionContributions
            .getTypeConfiguration()
            .getBasicTypeRegistry()
            .resolve(StandardBasicTypes.DOUBLE);

        functionContributions.getFunctionRegistry()
            .registerPattern("match", "match(?1) against (?2 in boolean mode)", resultType);
    }
}