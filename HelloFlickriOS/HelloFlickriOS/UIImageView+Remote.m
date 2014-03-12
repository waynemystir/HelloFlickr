//
//  UIImageView+Remote.m
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/8/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import "UIImageView+Remote.h"
#import "FTWCache.h"
#import <objc/runtime.h>

static char URL_KEY;

@implementation UIImageView (Remote)

@dynamic imageURL;

- (void) loadImageFromURL:(NSURL*) pURL placeholderImage:(UIImage*)pPlaceholder cachingKey:(NSString*)pKey {
    //load from cache
    UIImage *cachedImage = [UIImage imageWithData:[FTWCache objectForKey: pKey]];
    if (cachedImage) {
        self.image = cachedImage;
        return;
    }
    
    //variables
    self.imageURL = pURL;
	self.image = pPlaceholder;
    
    //thread
	dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0ul);
	dispatch_async(queue, ^{
        //download
		NSData *data = [NSData dataWithContentsOfURL:pURL];
        
        //store file
		UIImage *imageFromData = [UIImage imageWithData:data];
		[FTWCache setObject:UIImagePNGRepresentation(imageFromData) forKey:pKey];
        
        //set image if it still matches
		if (imageFromData) {
			if ([self.imageURL.absoluteString isEqualToString:pURL.absoluteString]) {
				dispatch_async(dispatch_get_main_queue(), ^{
					self.image = imageFromData;
				});
			}
		} else {
            NSLog(@"No Data!");
        }
        
        //reset url
		self.imageURL = nil;
	});
}

- (void) setImageURL:(NSURL *)newImageURL {
    //assossiated object
	objc_setAssociatedObject(self, &URL_KEY, newImageURL, OBJC_ASSOCIATION_COPY);
}

- (NSURL*) imageURL {
    //return
	return objc_getAssociatedObject(self, &URL_KEY);
}

@end
