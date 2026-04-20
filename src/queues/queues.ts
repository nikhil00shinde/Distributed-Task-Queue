import { createQueue } from './queue.factory.js';
import { QUEUE_NAMES } from '../jobs/job.types.js';

import {
  criticalQueueOptions,
  defaultQueueOptions,
  bulkQueueOptions,
} from '../jobs/job.options.js';
import type { JobPayload } from '../jobs/job.types.js';

// Three queue with different default options 
export const criticalQueue = createQueue<JobPayload>(
  QUEUE_NAMES.CRITICAL,
  criticalQueueOptions
);

export const defaultQueue = createQueue<JobPayload>(
  QUEUE_NAMES.DEFAULT,
  defaultQueueOptions
);

export const bulkQueue = createQueue<JobPayload>(
  QUEUE_NAMES.BULK,
  bulkQueueOptions
);

// Convenience map for looking up queues by name 
export const queues = {
  [QUEUE_NAMES.CRITICAL]: criticalQueue,
  [QUEUE_NAMES.DEFAULT]:  defaultQueue,
  [QUEUE_NAMES.BULK]: bulkQueue,
} as const;

// Graceful shutdown helper 
export async function closeAllQueues(): Promise<void> {
  await Promise.all([
    criticalQueue.close(),
    defaultQueue.close(),
    bulkQueue.close(),
  ]);
}

