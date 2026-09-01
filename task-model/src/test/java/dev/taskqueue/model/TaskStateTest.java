package dev.taskqueue.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class TaskStateTest {

  @Test 
  void queuedTaskCanStartProcessing() {
    assertTrue(TaskState.QUEUED.canTransitionTo(TaskState.PROCESSING));

    assertFalse(TaskState.QUEUED.canTransitionTo(TaskState.COMPLETED));
    assertFalse(TaskState.QUEUED.canTransitionTo(TaskState.DEAD_LETTERED));

  }

  @Test
  void processingTaskCanReachAllProcessingOutcomes() {
    assertTrue(TaskState.PROCESSING.canTransitionTo(TaskState.COMPLETED));
    assertTrue(TaskState.PROCESSING.canTransitionTo(TaskState.RETRY_WAIT));
    assertTrue(TaskState.PROCESSING.canTransitionTo(TaskState.DEAD_LETTERED));
  }

  @Test
  void retryWaitCanOnlyReturnToQueued() {
    assertTrue(TaskState.RETRY_WAIT.canTransitionTo(TaskState.QUEUED));

    assertFalse(TaskState.RETRY_WAIT.canTransitionTo(TaskState.PROCESSING));
    assertFalse(TaskState.RETRY_WAIT.canTransitionTo(TaskState.COMPLETED));
  }


  @Test
  void terminalStatesCannotTransition() {
    assertTrue(TaskState.COMPLETED.isTerminal());
    assertTrue(TaskState.DEAD_LETTERED.isTerminal());

    assertFalse(TaskState.QUEUED.isTerminal());
    assertFalse(TaskState.PROCESSING.isTerminal());
    assertFalse(TaskState.RETRY_WAIT.isTerminal());

    assertFalse(TaskState.COMPLETED.canTransitionTo(TaskState.QUEUED));
    assertFalse(TaskState.DEAD_LETTERED.canTransitionTo(TaskState.QUEUED));
  }
}


