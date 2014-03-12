//
//  FlickrPhoto.m
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/7/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import "FlickrPhoto.h"

@implementation FlickrPhoto

- (id)initWithId:(NSString *)id owner:(NSString *)owner secret:(NSString *)secret server:(NSString *)server farm:(NSString *)farm title:(NSString *)title ispublic:(NSNumber *)ispublic isfriend:(NSNumber *)isfriend isfamily:(NSNumber *)isfamily
{
    self = [super init];
    if (self) {
        _id = id;
        _owner = owner;
        _secret = secret;
        _server = server;
        _farm = farm;
        _title = title;
        _ispublic = ispublic;
        _isfriend = isfriend;
        _isfamily = isfamily;
    }
    return self;
}

- (NSString *)getUrl
{
    return [NSString stringWithFormat:@"http://farm%@.staticflickr.com/%@/%@_%@.jpg", _farm, _server, _id, _secret];
}

@end
