import {
  BadRequestException,
  Body,
  Controller,
  Get,
  Post,
  Query,
} from '@nestjs/common';

import { Beacon } from './beacon.schema';
import { BeaconsService } from './beacons.service';
import { CreateBeaconDto, FindBeaconsDto, FindMyBeaconsDto } from './dto';

@Controller('beacons')
export class BeaconsController {
  constructor(private readonly beaconsService: BeaconsService) {}

  @Post()
  async create(@Body() createBeaconInput: CreateBeaconDto): Promise<Beacon> {
    try {
      const beacon = await this.beaconsService.create(createBeaconInput);
      return beacon;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }

  @Get()
  async find(@Query() findBeaconInput: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconsService.find(findBeaconInput);
  }

  @Get('/my_beacons')
  async find_my_beacons(
    @Query() findMyBeaconInput: FindMyBeaconsDto,
  ): Promise<Beacon[]> {
    return this.beaconsService.findByCreatorId(findMyBeaconInput);
  }
}
