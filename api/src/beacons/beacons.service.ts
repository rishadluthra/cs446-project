import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';

import { Beacon, BeaconDocument } from './beacon.schema';
import { CreateBeaconDto, FindBeaconsDto } from './dto';

import { GeogratisService } from '../geogratis/geogratis.service';

@Injectable()
export class BeaconsService {
  constructor(
    @InjectModel(Beacon.name)
    private readonly beaconModel: Model<BeaconDocument>,
    private readonly geoGratisService: GeogratisService,
  ) {}

  async create(
    { postalCode, ...beaconBody }: CreateBeaconDto,
    creatorId: string,
  ): Promise<Beacon> {
    const coordinates =
      await this.geoGratisService.getCoordinatesFromPostalCode(postalCode);

    if (!coordinates) {
      throw new Error('Invalid postal code');
    }

    return this.beaconModel.create({
      creatorId,
      location: { type: 'Point', coordinates },
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

  async findByCreatorId(creatorId: string): Promise<Beacon[]> {
    return this.beaconModel.find({ creatorId });
  }

  async delete(creatorId: string, id: string): Promise<Beacon> {
    return this.beaconModel.findOneAndDelete({ id, creatorId });
  }
}
