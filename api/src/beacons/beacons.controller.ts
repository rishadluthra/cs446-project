import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';

import { Beacon } from './beacon.schema';
import { BeaconsService } from './beacons.service';
import { CreateBeaconDto, FindBeaconsDto } from './dto';
import { CurrentUser } from '../decorators/user.decorator';
import { User } from '../users/user.schema';
import { JwtAuthGuard } from '../auth/jwt-auth.guard';

@Controller('beacons')
export class BeaconsController {
  constructor(private readonly beaconsService: BeaconsService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
      @Body() createBeaconInput: CreateBeaconDto,
      @CurrentUser() currentUser: Partial<User>
    ): Promise<Beacon> {
    try {
      const beacon = await this.beaconsService.create(createBeaconInput, currentUser.id);
      return beacon;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }

  @Get()
  async find(@Query() findBeaconInput: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconsService.find(findBeaconInput);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/my_beacons')
  async find_my_beacons(
    @CurrentUser() currentUser: Partial<User>
  ): Promise<Beacon[]> {
    return this.beaconsService.findByCreatorId(currentUser.id);
  }

  @Delete(':id')
  async delete_beacons(@Param('id') id: string): Promise<Beacon> {
    return this.beaconsService.delete(id);
  }
}
