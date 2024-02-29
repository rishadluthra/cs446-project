import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

import { CreateBeaconDto } from './dto/create-beacon.dto';
import { Beacon } from './entities/beacon.entity';

@Injectable()
export class BeaconsService {
  constructor(
    @InjectRepository(Beacon)
    private beaconsRepository: Repository<Beacon>,
  ) {}

  async create(createBeaconDto: CreateBeaconDto): Promise<Beacon> {
    return this.beaconsRepository.save(createBeaconDto);
  }

  async findAll(): Promise<Beacon[]> {
    return this.beaconsRepository.find();
  }
}
