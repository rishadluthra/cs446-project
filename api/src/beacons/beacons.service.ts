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

  async create({
    postalCode,
    ...beaconBody
  }: CreateBeaconDto,
  creatorId: String): Promise<Beacon> {
    const coordinates =
      await this.geoGratisService.getCoordinatesFromPostalCode(postalCode);

    if (!coordinates) {
      throw new Error('Invalid postal code');
    }
    console.log("Inside Create:")
    console.log(creatorId)

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

  async findByCreatorId(creatorId: String): Promise<Beacon[]> {
    console.log("Inside findByCreatorId:")
    console.log(creatorId);
    return this.beaconModel.find({creatorId});
  }

  async delete(id: string): Promise<Beacon> {
    return this.beaconModel.findByIdAndDelete(id);
  }
}
