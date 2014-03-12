//
//  ClassDDD.m
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/12/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import "ClassDDD.h"

@interface ClassDDD ()

@property (nonatomic) int zeeInt;
@property (nonatomic, strong) NSObject *zeeObject;
@property NSString *zeeString;

@end

@implementation ClassDDD
{
    int someInt;
    NSObject *someObject;
}

- (void)play
{
    someInt = 4;
    someObject = [NSObject new];
    [self setZeeInt:4];
    [self setZeeObject:[NSObject new]];
    NSObject *wtf = [self zeeObject];
    [wtf setValue:@5 forKey:@"string"];
}

@end
