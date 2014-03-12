//
//  Cell.m
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/7/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import "Cell.h"

@implementation Cell

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.theImage = [[UIImageView alloc] initWithFrame:CGRectMake(0.0, 0.0, frame.size.width, frame.size.height)];
        //self.theImage.contentMode = UIViewContentModeScaleAspectFill;
        //self.theImage.clipsToBounds = YES;
        self.theImage.contentMode = UIViewContentModeScaleAspectFit;
        
        [self.contentView addSubview:self.theImage];;
    }
    return self;
}

@end
