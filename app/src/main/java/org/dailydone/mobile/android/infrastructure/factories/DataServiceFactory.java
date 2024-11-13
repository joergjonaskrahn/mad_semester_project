package org.dailydone.mobile.android.infrastructure.factories;

import org.dailydone.mobile.android.infrastructure.databases.TodoDatabase;
import org.dailydone.mobile.android.infrastructure.rest.ITodoRestOperations;
import org.dailydone.mobile.android.infrastructure.services.DistributedTodoDataService;
import org.dailydone.mobile.android.infrastructure.services.ITodoDataService;
import org.dailydone.mobile.android.infrastructure.services.LocalTodoDataService;

public class DataServiceFactory {
    private final TodoDatabase todoDatabase;
    private final ITodoRestOperations todoRestOperations;

    public DataServiceFactory(TodoDatabase todoDatabase, ITodoRestOperations todoRestOperations) {
        this.todoDatabase = todoDatabase;
        this.todoRestOperations = todoRestOperations;
    }

    public ITodoDataService createDataService(boolean isWebBackendAvailable) {
        if (isWebBackendAvailable) {
            DistributedTodoDataService distributedTodoDataService =
                    new DistributedTodoDataService(todoDatabase, todoRestOperations);
            // Trigger synchronization of local and remote Data Source if the Web Backend
            // is available
            distributedTodoDataService.synchronizeDataSources();
            return distributedTodoDataService;
        } else {
            return new LocalTodoDataService(todoDatabase);
        }
    }
}