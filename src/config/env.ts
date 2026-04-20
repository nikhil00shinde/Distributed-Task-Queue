import 'dotenv/config';
import { z } from 'zod';

const envSchema = z.object({
  NODE_ENV: z.enum(['development','production','test']).default('development'),
  REDIS_HOST: z.string().default('localhost'),
  REDIS_PORT: z.coerce.number().int().positive().default(6379),
  LOG_LEVEL: z.enum(['debug','info','warn','error']).default('info'),
});

//Parse and validate. If invalid, app crashes immediately witha clear error.

const parsed = envSchema.safeParse(process.env);

if (!parsed.success) {
  console.log('Invalid environment variables');
  console.log(parsed.error.flatten().fieldErrors);
  process.exit(1);
}

export const env = parsed.data
export type Env = z.infer<typeof envSchema>;
