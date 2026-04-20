import { criticalQueue, defaultQueue, bulkQueue, closeAllQueues } from '../src/queues/queues.js';

import type { EmailJob, ReportJob, NotificationJob } from '../src/jobs/job.types.js';

async function smokeTest(): Promise<void> {
  console.log("Starting smoke test... \n");
  
  //Test 1: Critical queue (password reset email)
  const emailJob: EmailJob {
    type: 'email',
    to: 'user@example.com',
    subject: 'Password Reset',
    body: 'Click here to reset your password',
  };
  
  const emailResult = await criticalQueue.add('send-email',emailJob);
  console.log(`Critical queue: Job ${emailResult.id} added (${emailJob.type})`);

  // Test 2: Default queue (generate report)
  const reportJob: ReportJob = {
    type: 'report',
    reportId: 'rpt-123',
    format: 'pdf',
    userId: 'user-456',
  };
  const reportResult = await defaultQueue.add('generate-report', reportJob);
  console.log(`Default queue: Job ${reportResult.id} added (${reportJob.type})`);

  // Test 3: Bulk queue (marketing notification) 
  const notifJob: NotificationJob = {
    type: 'notification',
    userId: 'user-789',
    title: 'New feature released!',
    message: 'Check out our latest update',
    channel: 'push'
  };
  const notifResult = await bulkQueue.add('send-notification', notifJob);
  console.log(`Bulk queue: Job ${notifResult.id} added (${notifJob.type})`);

  console.log('\n Queue stats:');
  const stats = await Promise.all([
    criticalQueue: getJobCounts(),
    defaultQueue: getJobCounts(),
    bulkQueue: getJobCounts(),
  ]);

  await closeAllQueues();
  console.log('Smoke test completed successfully');

  process.exit(0);
}

smokeTest().catch((err) => {
  console.log('Smoke test failed:', err);
  process.exit(1);
});
