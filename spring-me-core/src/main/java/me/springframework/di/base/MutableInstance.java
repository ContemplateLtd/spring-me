/**
 * Copyright (C) 2009 Original Authors
 *
 * This file is part of Spring ME.
 *
 * Spring ME is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2, or (at your option) any
 * later version.
 *
 * Spring ME is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Spring ME; see the file COPYING. If not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library. Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module. An independent module is a module which is not derived from or
 * based on this library. If you modify this library, you may extend this
 * exception to your version of the library, but you are not obligated to
 * do so. If you do not wish to do so, delete this exception statement
 * from your version.
 */
package me.springframework.di.base;

import me.springframework.di.Instance;
import me.springframework.di.Scope;
import me.springframework.di.Sink;

import java.util.List;
import java.util.Set;


public class MutableInstance extends AbstractTyped implements Instance, MutableSource {

    private List<MutableConstructorArgument> constructorArguments;

    private String name;

    private Set<MutablePropertySetter> propertySetters;

    private String id;

    private String initMethod;

    private String factoryMethod;

    private String factoryInstance;

    private String referencedType;

    private boolean lazyInit;

    private Sink sink;

    private String destroyMethod;

    private boolean autowireCandidate;

    private int autowireMode;

    private boolean isFactoryBean;

    private Scope scope = Scope.SINGLETON;

    public MutableInstance(String name) {
        this.name = name;
    }

    public MutableInstance(Sink sink, String name) {
        this.sink = sink;
        this.name = name;
    }

    public List<MutableConstructorArgument> getConstructorArguments() {
        return constructorArguments;
    }

    public void setConstructorArguments(List<MutableConstructorArgument> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<MutablePropertySetter> getSetters() {
        return propertySetters;
    }

    public void setSetters(Set<MutablePropertySetter> propertySetters) {
        this.propertySetters = propertySetters;
    }

    public boolean isAnonymous() {
        return name == null;
    }

    public SourceType getSourceType() {
        return SourceType.Instance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setInitMethod(String initMethod) {
        this.initMethod = initMethod;
    }

    public String getInitMethod() {
        return initMethod;
    }

    public String getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(String factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public String getFactoryInstance() {
        return factoryInstance;
    }

    public void setFactoryInstance(String factoryInstance) {
        this.factoryInstance = factoryInstance;
    }

    public String getReferencedType() {
        return referencedType;
    }

    public void setReferencedType(String referencedType) {
        this.referencedType = referencedType;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public String toString() {
        if (sink == null) {
            return "bean " + name;
        } else {
            return "a bean of type " + getType() + ", injected into " + sink.toString();
        }
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public boolean isAutowireCandidate() {
        return autowireCandidate;
    }

    public void setAutowireCandidate(boolean autowireCandidate) {
        this.autowireCandidate = autowireCandidate;
    }

    public int getAutowireMode() {
        return autowireMode;
    }

    public void setAutowireMode(int autowireMode) {
        this.autowireMode = autowireMode;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public boolean isPrototype() {
        return Scope.PROTOTYPE == scope;
    }

    public boolean isSingleton() {
        return Scope.SINGLETON == scope;
    }

    public boolean isFactoryBean() {
        return isFactoryBean;
    }

    public void setFactoryBean(boolean isFactoryBean) {
        this.isFactoryBean = isFactoryBean;
    }

}
