import { Module } from '@nestjs/common';

import { GeogratisService } from './geogratis.service';

@Module({
  providers: [GeogratisService],
  exports: [GeogratisService],
})
export class GeogratisModule {}
