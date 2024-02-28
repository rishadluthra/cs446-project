import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Beacon, BeaconDocument } from './beacon.schema';
import { CreateBeaconDto, FindBeaconsDto } from './dto';

@Injectable()
export class BeaconsService {
  constructor(
    @InjectModel(Beacon.name)
    private readonly beaconModel: Model<BeaconDocument>,
  ) {}

  async create({ location, ...beaconBody }: CreateBeaconDto): Promise<Beacon> {
    return this.beaconModel.create({
      location: {
        type: 'Point',
        coordinates: [location.latitude, location.longitude],
      },
      ...beaconBody,
    });
  }

  async find({
    latitude,
    longitude,
    maxDistance,
  }: FindBeaconsDto): Promise<Beacon[]> {
    return this.beaconModel
      .find({
        location: {
          $near: {
            $geometry: {
              type: 'Point',
              coordinates: [latitude, longitude],
            },
            $maxDistance: maxDistance,
          },
        },
      })
      .exec();
  }
}
