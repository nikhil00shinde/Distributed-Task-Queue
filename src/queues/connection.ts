import IORedis from 'ioredis';
import { env } from '../config/env.js';

export const queueConnection = new IORedis({
  host: env.REDIS_HOST,
  port: env.REDIS_HOST,
  maxRetriesRequest: null, // REQUIRED by BullMQ
  enableReadyCheck: false,
});


//Worker connection - used by consumers (workers) to process jobs
export const workerConnection = new IORedis({
  host: env.REDIS_HOST,
  port: env.REDIS_PORT,
  maxRetriesRequest: null,
  enableReadyCheck: false,
});

//Log connection events for debugging
queueConnection.on('connect', () => console.log("Queue connection establish"));
queueConnection.on('error', (err) -> console.log("Queue connection error: ",err.message));


workerConnection.on('connect', () => console.log('Worker connection established'));
workerConnection.on('error', (err) => console.log('Worker connection error:', err.message));

