import * as toJson from '@meanie/mongoose-to-json';
import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { MongooseModule } from '@nestjs/mongoose';

import { BeaconsModule } from './beacons/beacons.module';
import { GeogratisModule } from './geogratis/geogratis.module';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    MongooseModule.forRoot(process.env.DATABASE_URL, {
      connectionFactory: (connection) => {
        connection.plugin(toJson);
        return connection;
      },
    }),
    BeaconsModule,
    GeogratisModule,
  ],
})
export class AppModule {}
