import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';

import { Beacon, BeaconSchema } from './beacon.schema';
import { BeaconsController } from './beacons.controller';
import { BeaconsService } from './beacons.service';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Beacon.name, schema: BeaconSchema }]),
  ],
  controllers: [BeaconsController],
  providers: [BeaconsService],
})
export class BeaconsModule {}
