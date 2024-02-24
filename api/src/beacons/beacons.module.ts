import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';

import { BeaconsController } from './beacons.controller';
import { BeaconsService } from './beacons.service';
import { Beacon } from './entities/beacon.entity';

@Module({
  imports: [TypeOrmModule.forFeature([Beacon])],
  controllers: [BeaconsController],
  providers: [BeaconsService],
})
export class BeaconsModule {}
