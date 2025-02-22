/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2023 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jkiss.dbeaver.registry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.jkiss.dbeaver.model.edit.DBEObjectManager;
import org.jkiss.dbeaver.model.impl.AbstractDescriptor;

/**
 * ObjectManagerDescriptor
 */
public class ObjectManagerDescriptor extends AbstractDescriptor
{
    public static final String EXTENSION_ID = "org.jkiss.dbeaver.objectManager"; //NON-NLS-1 //$NON-NLS-1$

    private String id;
    private ObjectType managerType;
    private ObjectType objectType;
    private DBEObjectManager managerInstance;

    ObjectManagerDescriptor(IConfigurationElement config)
    {
        super(config);

        this.id = config.getAttribute(RegistryConstants.ATTR_CLASS);
        this.managerType = new ObjectType(id);
        this.objectType = new ObjectType(config.getAttribute(RegistryConstants.ATTR_OBJECT_TYPE));
    }

    void dispose()
    {
        objectType = null;
        managerType = null;
        managerInstance = null;
    }

    public String getId()
    {
        return id;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public boolean appliesToType(Class<?> clazz)
    {
        return objectType.matchesType(clazz);
    }

    public synchronized DBEObjectManager getManager()
    {
        if (managerInstance != null) {
            return managerInstance;
        }
        Class<? extends DBEObjectManager> clazz = managerType.getObjectClass(DBEObjectManager.class);
        if (clazz == null) {
            throw new IllegalStateException("Can't instantiate object manager '" + managerType.getImplName() + "'");
        }
        try {
            managerInstance = clazz.getConstructor().newInstance();
        } catch (Throwable ex) {
            throw new IllegalStateException("Error instantiating object manager '" + clazz.getName() + "'", ex);
        }
        return managerInstance;
    }

    @Override
    public String toString() {
        return id;
    }

}