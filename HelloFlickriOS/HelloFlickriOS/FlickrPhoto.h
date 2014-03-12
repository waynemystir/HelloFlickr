//
//  FlickrPhoto.h
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/7/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface FlickrPhoto : NSObject

- (id)initWithId:(NSString *)id owner:(NSString *)owner secret:(NSString *)secret server:(NSString *)server farm:(NSString *)farm title:(NSString *)title ispublic:(NSNumber *)ispublic isfriend:(NSNumber *)isfriend isfamily:(NSNumber *)isfamily;

- (NSString *)getUrl;

@property (nonatomic, strong) NSString *id;
@property (nonatomic, strong) NSString *owner;
@property (nonatomic, strong) NSString *secret;
@property (nonatomic, strong) NSString *server;
@property (nonatomic, strong) NSString *farm;
@property (nonatomic, strong) NSString *title;
@property (nonatomic) NSNumber *ispublic;
@property (nonatomic) NSNumber *isfriend;
@property (nonatomic) NSNumber *isfamily;

@end
