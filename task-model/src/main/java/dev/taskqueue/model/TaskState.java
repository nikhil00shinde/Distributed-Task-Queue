package dev.taskqueue.model;


// Defining the state the queue can be in 

public enum TaskState {
  QUEUED,
  PROCESSING,
  RETRY_WAIT,
  COMPLETED,
  DEAD_LETTERED;

  public boolean canTransitionTo(TaskState next){
    return switch (this) {
      case QUEUED ->
            next == PROCESSING;
      
      case PROCESSING -> 
            next == COMPLETED || next == RETRY_WAIT || next == DEAD_LETTERED;

      case RETRY_WAIT -> 
            next == QUEUED;

      case COMPLETED, DEAD_LETTERED -> 
            false;
    };
  }

  public boolean isTerminal() {
    return this == COMPLETED || this == DEAD_LETTERED;
  }
}
