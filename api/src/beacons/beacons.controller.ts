import {
  BadRequestException,
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';

import { Beacon } from './beacon.schema';
import { BeaconsService } from './beacons.service';
import { CreateBeaconDto, FindBeaconsDto } from './dto';

import { JwtAuthGuard } from '../auth/jwt-auth.guard';
import { CurrentUser } from '../decorators/user.decorator';
import { User } from '../users/user.schema';

@Controller('beacons')
export class BeaconsController {
  constructor(private readonly beaconsService: BeaconsService) {}

  @UseGuards(JwtAuthGuard)
  @Post()
  async create(
    @CurrentUser() currentUser: Partial<User>,
    @Body() createBeaconInput: CreateBeaconDto,
  ): Promise<Beacon> {
    try {
      const beacon = await this.beaconsService.create(
        createBeaconInput,
        currentUser.id,
      );
      return beacon;
    } catch (error) {
      throw new BadRequestException(error.message);
    }
  }

  @UseGuards(JwtAuthGuard)
  @Get()
  async find(@Query() findBeaconInput: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconsService.find(findBeaconInput);
  }

  @UseGuards(JwtAuthGuard)
  @Get('/my_beacons')
  async find_my_beacons(
    @CurrentUser() currentUser: Partial<User>,
  ): Promise<Beacon[]> {
    return this.beaconsService.findByCreatorId(currentUser.id);
  }

  @UseGuards(JwtAuthGuard)
  @Patch(':id')
  async update(
    @CurrentUser() currentUser: Partial<User>,
    @Param('id') id: string,
    @Body() updateBeaconInput: CreateBeaconDto,
  ): Promise<Beacon> {
    return this.beaconsService.update(id, updateBeaconInput, currentUser.id);
  }

  @UseGuards(JwtAuthGuard)
  @Delete(':id')
  async delete(
    @CurrentUser() currentUser: Partial<User>,
    @Param('id') id: string,
  ): Promise<Beacon> {
    return this.beaconsService.delete(id, currentUser.id);
  }
}
