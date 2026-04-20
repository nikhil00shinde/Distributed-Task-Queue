import type { JobsOptions } from 'bullmq';

export const PRIORITY = {
  CRITICAL: 1,
  DEFAULT: 5,
  BULK: 10,
} as const;


// Retry config: exponential backoff
// Attemp 1 fails -> wait 1s -> attemp 2 fails -> wait 2s -> attempt 3 fails -> wait 2s -> attempt 3 fails -> wait 4s -> ...
// Delays: 1s, 2s, 4s, 8s, 16s (5 total attempts)

export const RETRY_CONFIG: JobsOption = {
  attempts: 5,
  backoff: {
    type: 'exponential',
    delay: 1000, //starting delay in ms
  },
  removeOnComplete: {
    age: 3600,  //Keep completed jobs for 1 hour
    count: 1000, // Or keep last 1000 (whichever comes first)
  }, 
  removeOnFail: {
    age: 86400, // Keep failed jobs for 24 hours (for debugging)
  },
};

//Per-queue defaults 
export const criticalQueueOptions: JobOptions = {
  ...RETRY_CONFIG,
  priority: PRIORITY,.CRITICAL,
};

export const defaultQueueOptions: JobsOptions = {
  ...RETRY_CONFIG,
  priority: PRIORITY.DEFAULT,
};

export const buldQueueOptions: JobsOptions = {
  ...RETRY_CONFIG,
  priority: PRIORITY.BULK,
};
