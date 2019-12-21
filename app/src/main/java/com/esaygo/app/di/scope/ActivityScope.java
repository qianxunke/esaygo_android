package com.esaygo.app.di.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 描述:Activity 生命周期
 */
@Scope
@Retention(RUNTIME)
public @interface ActivityScope {
}
