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
package me.springframework.di.spring;

import static java.util.Collections.singletonList;
import static me.springframework.di.Source.SourceType.StringRepresentation;

import java.util.Collection;
import java.util.Map;

import me.springframework.di.LiteralSource;
import me.springframework.di.Sink;
import me.springframework.di.Source;
import me.springframework.di.base.AbstractSink;
import me.springframework.di.base.MutableConstructorArgument;
import me.springframework.di.base.MutableContext;
import me.springframework.di.base.MutableInstance;
import me.springframework.di.base.MutableInstanceReference;
import me.springframework.di.base.MutableStringValueSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Augments sinks with information about any type casts required at that sink.
 * <p>
 * This must be done after the {@link QDoxAugmentation} because it relies on knowing
 * whether a source refers to a {@link FactoryBean}.  The QDoxAugmentation cannot
 * guarantee to process FactoryBean definitions before the bean definitions in which they
 * are used.  The augmentation should also be applied after the {@link AutowiringAugmentation},
 * because that augmentation can introduce new sinks.
 */
public class SinkAugmentation implements Augmentation {

    public void augment(MutableContext context) {
        Map<String, MutableInstance> instances = context.getInstances();
        for (MutableInstance instance : instances.values()) {
            attribute(instance, context);
        }
    }

    private void attribute(MutableInstance instance, MutableContext context) {
        attribute(instance.getConstructorArguments(), context);
        attribute(instance.getSetters(), context);
    }

    /**
     * Sets the {@link AbstractSink#setCastRequired(boolean)} flag on the given
     * {@link Sink} instances.
     */
    private void attribute(Collection<? extends AbstractSink> sinks, MutableContext context) {
        if (sinks == null || sinks.size() == 0) {
            return;
        }

        for (AbstractSink sink : sinks) {
            attribute(context, sink);

            Source source = sink.getSource();
            if (source instanceof MutableInstance) {
                attribute((MutableInstance) source, context);
            }
        }
    }

    protected void attribute(MutableContext context, AbstractSink sink) {
        Source source = sink.getSource();
        String type = sink.getType();

        if (isFactoryBean(source, context)) {
            sink.setCastTo(type.replace('$', '.'));
        }

        if (isImplicitResource(sink)) {
            String file = ((LiteralSource) source).getValue();
            convertStringToResource(sink, file);
        }

        if (type != null) {
            if (type.endsWith("[]")) {
                sink.setArray(true);
                String elementType = type.substring(0, type.length() - 2);
                sink.setType(elementType);
            }
        }
    }

    /**
     * Returns true if a given source refers to a {@link FactoryBean}.
     *
     * @param source Any type of Source instance.
     * @param instances All known instances.
     * @return True if the source refers to a FactoryBean, otherwise false.
     */
    private boolean isFactoryBean(Source source, MutableContext context) {
        boolean factoryBean = false;
        if (source instanceof MutableInstanceReference) {
            MutableInstanceReference r = (MutableInstanceReference) source;
            MutableInstance inst = context.getByName(r.getName());
            if (inst != null) {
                factoryBean = inst.isFactoryBean();
            }
        } else if (source instanceof MutableInstance) {
            MutableInstance r = (MutableInstance) source;
            factoryBean = r.isFactoryBean();
        }
        return factoryBean;
    }

    /**
     * Returns true if the sink expects a Resource instance, and its source is
     * a string literal (the file name).
     *
     * @param sink The sink.
     * @return True if the an implicit Resource should be created for the given
     *         file name.
     */
    private boolean isImplicitResource(Sink sink) {
        return Resource.class.getName().equals(sink.getType())
                && StringRepresentation.equals(sink.getSource().getSourceType());
    }

    /**
     * Sets the source for a sink to a {@link Resource} instance.
     *
     * @param sink The sink into which a Resource instance should be injected.
     * @param file The file name that should be used for the injected resource.
     */
    private void convertStringToResource(AbstractSink sink, String file) {
        MutableInstance resource = createResource(sink, file);
        sink.setSource(resource);
        sink.setType(Resource.class.getName());
    }

    /**
     * Creates an instance for a {@link Resource}. The returned instance is a
     * ClassPathResource with a single argument. The value of the argument is
     * the given file name.
     *
     * @param sink The sink that requires a Resource type.
     * @param id   The identifier of the instance to create.
     * @param file The filename of the resource.
     * @return An instance denoting the injected Resource.
     */
    private MutableInstance createResource(Sink sink, String file) {
        MutableInstance resource = new MutableInstance(sink, null);
        MutableConstructorArgument ctrArgSink =
                new MutableConstructorArgument(resource);
        MutableStringValueSource ctrArgSource =
                new MutableStringValueSource(sink, file, String.class.getName());
        ctrArgSink.setSource(ctrArgSource);
        resource.setConstructorArguments(singletonList(ctrArgSink));
        resource.setId(sink.getSource().getId());
        resource.setType(ClassPathResource.class.getName());
        return resource;
    }

}
