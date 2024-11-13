package org.dailydone.mobile.android.factories;

import org.dailydone.mobile.android.databases.TodoDatabase;
import org.dailydone.mobile.android.rest.ITodoRestOperations;
import org.dailydone.mobile.android.services.DistributedTodoDataService;
import org.dailydone.mobile.android.services.ITodoDataService;
import org.dailydone.mobile.android.services.LocalTodoDataService;

public class DataServiceFactory {
    private final TodoDatabase todoDatabase;
    private final ITodoRestOperations todoRestOperations;

    public DataServiceFactory(TodoDatabase todoDatabase, ITodoRestOperations todoRestOperations) {
        this.todoDatabase = todoDatabase;
        this.todoRestOperations = todoRestOperations;
    }

    public ITodoDataService createDataService(boolean isWebBackendAvailable) {
        if (isWebBackendAvailable) {
            return new DistributedTodoDataService(todoDatabase, todoRestOperations);
        } else {
            return new LocalTodoDataService(todoDatabase);
        }
    }
}