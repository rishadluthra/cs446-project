import { Body, Controller, Get, Post, Query } from '@nestjs/common';

import { Beacon } from './beacon.schema';
import { BeaconsService } from './beacons.service';
import { CreateBeaconDto, FindBeaconsDto } from './dto';

@Controller('beacons')
export class BeaconsController {
  constructor(private readonly beaconsService: BeaconsService) {}

  @Post()
  create(@Body() createBeaconInput: CreateBeaconDto): Promise<Beacon> {
    return this.beaconsService.create(createBeaconInput);
  }

  @Get()
  find(@Query() findBeaconInput: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconsService.find(findBeaconInput);
  }
}
