import * as toJson from '@meanie/mongoose-to-json';
import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';

import { AuthModule } from './auth/auth.module';
import { BeaconsModule } from './beacons/beacons.module';
import { EmailModule } from './email/email.module';
import { GeogratisModule } from './geogratis/geogratis.module';
<<<<<<< HEAD
import { ReportsModule } from './reports/reports.module';
=======
>>>>>>> 1823a18 (Reviews backend (#5))
import { ReviewsModule } from './reviews/reviews.module';
import { UsersModule } from './users/users.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    MongooseModule.forRoot(process.env.DATABASE_URL, {
      connectionFactory: (connection) => {
        connection.plugin(toJson);
        return connection;
      },
    }),
    AuthModule,
    BeaconsModule,
    GeogratisModule,
<<<<<<< HEAD
    ReportsModule,
=======
>>>>>>> 1823a18 (Reviews backend (#5))
    ReviewsModule,
    EmailModule,
    UsersModule,
  ],
})
export class AppModule {}
