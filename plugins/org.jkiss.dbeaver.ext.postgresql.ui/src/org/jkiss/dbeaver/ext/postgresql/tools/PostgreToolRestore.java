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
package org.jkiss.dbeaver.ext.postgresql.tools;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.jkiss.dbeaver.DBException;
import org.jkiss.dbeaver.ext.postgresql.model.PostgreDatabase;
import org.jkiss.dbeaver.ext.postgresql.model.PostgreSchema;
import org.jkiss.dbeaver.model.DBUtils;
import org.jkiss.dbeaver.model.messages.ModelMessages;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.runtime.DBWorkbench;
import org.jkiss.dbeaver.tasks.ui.nativetool.NativeToolWizardDialog;
import org.jkiss.dbeaver.ui.tools.IUserInterfaceTool;

import java.util.Collection;

/**
 * Database import
 */
public class PostgreToolRestore implements IUserInterfaceTool
{
    @Override
    public void execute(IWorkbenchWindow window, IWorkbenchPart activePart, Collection<DBSObject> objects) throws DBException
    {
        for (DBSObject object : objects) {
            PostgreDatabase database;
            if (object instanceof PostgreSchema) {
                database = ((PostgreSchema) object).getDatabase();
            } else if (object instanceof PostgreDatabase) {
                database = (PostgreDatabase) object;
            } else {
                continue;
            }
            if (DBUtils.isReadOnly(database)) {
                DBWorkbench.getPlatformUI().showWarningMessageBox(ModelMessages.tasks_restore_readonly_title,
                    NLS.bind(ModelMessages.tasks_restore_readonly_message, database.getDataSource().getName()));
            } else {
                NativeToolWizardDialog dialog = new NativeToolWizardDialog(window, new PostgreRestoreWizard(database));
                dialog.open();
            }
        }
    }
}
