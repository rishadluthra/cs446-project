import { Body, Controller, Get, Post } from '@nestjs/common';

import { BeaconsService } from './beacons.service';
import { CreateBeaconDto } from './dto/create-beacon.dto';
import { Beacon } from './entities/beacon.entity';

@Controller('beacons')
export class BeaconsController {
  constructor(private readonly beaconsService: BeaconsService) {}

  @Post()
  async create(@Body() createBeaconDto: CreateBeaconDto): Promise<Beacon> {
    return this.beaconsService.create(createBeaconDto);
  }

  @Get()
  async findAll(): Promise<Beacon[]> {
    return this.beaconsService.findAll();
  }
}
