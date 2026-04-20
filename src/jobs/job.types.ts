
export type EmailJob = {
  type: 'email';
  to: string;
  subject: string;
  body: string;
  attachments?: string[];
};

export type ReportJob = {
  type: 'report';
  reportId: string;
  format: 'pdf' | 'csv' | 'xlsx';
  userId: string;
};

export type NotificationJob = {
  type: 'notification';
  userId: string;
  title: string;
  message: string;
  channel: 'push' | 'sms' | 'in-apps';
};

//Union of all job types
export type JobPayload = EmailJob | ReportJob | NotificationJob;

//Queue names as constants (avoid typos)
export const QUEUE_NAMES = {
  CRITICAL: 'critical',
  DEFAULT: 'default',
  BULK: 'bulk',
} as const;


export type QueueName = typeof QUEUE_NAMES[keyof typeof QUEUE_NAMES];
