//
//  UIImageView+Remote.h
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/8/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImageView (Remote)

@property (nonatomic, retain) NSURL * imageURL;

- (void) loadImageFromURL:(NSURL*) pURL placeholderImage:(UIImage*)pPlaceholder cachingKey:(NSString*)pKey;

@end
