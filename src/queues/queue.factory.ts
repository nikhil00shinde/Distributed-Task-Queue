import { Queue, QueueEvents } from 'bullmq';
import { queueConnection } from './connection.js';
import type { JobOptions } from 'bullmq';

//Generic Factory - creates a types Queue
export function createQueue<TData = unknown>(
  name: string,
  defaultJobOptions?: defaultJobOptions
): Queue<TData> {
  return new Queue<TData>(name, {
    connection: queueConnection,
    defaultJobOptions,
  });
}


// Creates a QueueEvents instance - used to listen to queue-level events 
// (jobs completed/failed across ALL workers)
export function createQueueEvents(name: string): QueueEvents {
  return new QueueEvents(name, {
    connection: queueConnection.duplicate(),
  });
}
