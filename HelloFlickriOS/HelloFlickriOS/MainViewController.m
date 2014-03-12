//
//  MainViewController.m
//  HelloFlickriOS
//
//  Created by WAYNE SMALL on 2/7/14.
//  Copyright (c) 2014 WAYNE SMALL. All rights reserved.
//

#import "MainViewController.h"
#import "FlickrPhoto.h"
#import "Cell.h"
#import "UIImageView+Remote.h"

#define THE_URL [NSString stringWithFormat:@"%@/rest?method=%@&api_key=%@&format=%@&nojsoncallback=%d", BASE_URL, METHOD, API_KEY, FORMAT, NON_JSON_CALLBACK]
#define kCellID @"cellID"

static NSString const *BASE_URL = @"http://api.flickr.com/services";
static NSString const *METHOD = @"flickr.interestingness.getList";
static NSString const *API_KEY = @"1eec2861941ba4c2a13c516116ce30b5";
static NSString const *FORMAT = @"json";
static int const NON_JSON_CALLBACK = 1;

@interface MainViewController ()

@property (nonatomic, strong) NSMutableArray *theData;
@property (nonatomic, strong) UICollectionView *collectionView;

@end

@implementation MainViewController

#pragma mark - View Lifecycle

/*- (id)init
{
    //self = [super initWithNibName:@"MainView" bundle:nil];
    self = [super initWithNibName:nil bundle:nil];
    if (self != nil)
    {
    }
    return self;
    
}*/

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.navigationItem.title = @"Hello Flickr Sample";
    [self doItWithNib];
    //[self doItProgrammatically];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Nib or Programmatic

- (void)doItWithNib
{
    UICollectionViewFlowLayout *aFlowLayout = [UICollectionViewFlowLayout new];
    [aFlowLayout setItemSize:CGSizeMake(100, 80)];
    [aFlowLayout setScrollDirection:UICollectionViewScrollDirectionVertical];
    
    self.collectionView = (UICollectionView *)[self.view viewWithTag:41];
    [self.collectionView setCollectionViewLayout:aFlowLayout];
    self.collectionView.backgroundColor = [UIColor whiteColor];
    [self.collectionView registerClass:[Cell class] forCellWithReuseIdentifier:kCellID];
    [self.collectionView setDataSource:self];
    [self.collectionView setDelegate:self];
    
    UIButton *gib = (UIButton *)[self.view viewWithTag:42];
    gib.backgroundColor = [MainViewController getButtonColorNormalState];
    [gib setTitle:@"Get NImages" forState:UIControlStateNormal];
    [gib setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [gib addTarget:self action:@selector(downButton:) forControlEvents:UIControlEventTouchDown];
    [gib addTarget:self action:@selector(upButton:) forControlEvents:UIControlEventTouchUpInside];
}


- (void)doItProgrammatically
{
    CGFloat ny = self.navigationController.navigationBar.frame.origin.y;
    CGFloat nh = self.navigationController.navigationBar.frame.size.height;
    
    CGFloat bs = 40.0f;
    CGFloat x = self.view.frame.origin.x;
    //CGFloat y = self.view.frame.origin.y;
    CGFloat w = self.view.frame.size.width;
    CGFloat h = self.view.frame.size.height;
    
    UICollectionViewFlowLayout *aFlowLayout = [UICollectionViewFlowLayout new];
    [aFlowLayout setItemSize:CGSizeMake(100, 80)];
    [aFlowLayout setScrollDirection:UICollectionViewScrollDirectionVertical];
    
    self.collectionView = [[UICollectionView alloc] initWithFrame:CGRectMake(x, ny+nh+bs, w, h - (ny+nh+bs)) collectionViewLayout:aFlowLayout];
    [self.collectionView setContentInset:UIEdgeInsetsMake(-ny-nh, 0, 0, 0)];
    self.collectionView.backgroundColor = [UIColor whiteColor];
    [self.collectionView registerClass:[Cell class] forCellWithReuseIdentifier:kCellID];
    [self.collectionView setDataSource:self];
    [self.collectionView setDelegate:self];
    [self.view addSubview:self.collectionView];
    
    UIButton *gib = [[UIButton alloc] initWithFrame:CGRectMake(x, ny+nh+3, 100, bs-6)];
    gib.backgroundColor = [MainViewController getButtonColorNormalState];
    [gib setTitle:@"Get Images" forState:UIControlStateNormal];
    [gib setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [gib addTarget:self action:@selector(downButton:) forControlEvents:UIControlEventTouchDown];
    [gib addTarget:self action:@selector(upButton:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:gib];
}

#pragma mark - Selectors

- (void)downButton:(id)sender
{
    BOOL sen = [sender isKindOfClass:[UIButton class]];
    if (!sen) {
        return;
    }
    
    UIButton *butt = (UIButton *)sender;
    [butt setTitleColor:[UIColor yellowColor] forState:UIControlStateNormal];
    [butt setBackgroundColor:[UIColor blackColor]];
}

- (void)upButton:(id)sender
{
    [self fetchImageUrls];
    
    BOOL sen = [sender isKindOfClass:[UIButton class]];
    if (!sen) {
        return;
    }
    
    UIButton *butt = (UIButton *)sender;
    [butt setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
    [butt setBackgroundColor:[MainViewController getButtonColorNormalState]];
}

+ (UIColor *)getButtonColorNormalState
{
    return [UIColor colorWithRed:(200/255.0) green:(200/255.0) blue:(200/255.0) alpha:1.0];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)view numberOfItemsInSection:(NSInteger)section;
{
    return [self.theData count];
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    Cell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:kCellID forIndexPath:indexPath];
    FlickrPhoto *fp = [self.theData objectAtIndex:indexPath.row];
    
    NSString *url = [fp getUrl];
    NSLog(@"CVCVCVCVCVCV %@", url);
    NSURL *imageURL = [NSURL URLWithString: url];
    NSArray *pathComponents = [url componentsSeparatedByString: @"/"];
    NSString *key = pathComponents.lastObject;
    [cell.theImage loadImageFromURL:imageURL placeholderImage: [UIImage imageNamed:@"sneaky_placeholder"] cachingKey: key];
    
    return cell;
}

#pragma mark - Data gathoring off main thread

- (void)fetchImageUrls
{
    NSMutableURLRequest *req = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:THE_URL]];
    [req setHTTPMethod:@"POST"];
    //NSURLConnection *conn = [[NSURLConnection alloc] initWithRequest:req delegate:nil startImmediately:YES];
    
    [NSURLConnection sendAsynchronousRequest:req queue:[[NSOperationQueue alloc] init] completionHandler:^(NSURLResponse *response, NSData *data, NSError *connectionError) {
        if ([data length] > 0 && connectionError == nil)
        {
            NSLog(@"something came back");
            NSDictionary *dataDictionaryResponse = [NSJSONSerialization JSONObjectWithData:data options:0 error:&connectionError];
            NSLog(@"%@",dataDictionaryResponse);
            [self updateList:dataDictionaryResponse];
        }
        else if ([data length] == 0 && connectionError == nil)
        {
            NSLog(@"Nothing was downloaded.");
        }
        else if (connectionError != nil)
        {
            NSLog(@"Error = %@", connectionError);
        }
    }];

}

- (void)updateList:(NSDictionary *)dict
{
    if (!self.theData) {
        self.theData = [[NSMutableArray alloc] init];
    }
    
    [self.theData removeAllObjects];
    id photos = [dict objectForKey:@"photos"];
    NSArray *arr = [photos objectForKey:@"photo"];
    for(NSDictionary *di in arr)
    {
        [self.theData addObject:[[FlickrPhoto alloc] initWithId:[di objectForKey:@"id"] owner:[di objectForKey:@"owner"] secret:[di objectForKey:@"secret"] server:[di objectForKey:@"server"] farm:[di objectForKey:@"farm"] title:[di objectForKey:@"title"] ispublic:[di objectForKey:@"ispublic"] isfriend:[di objectForKey:@"isfriend"] isfamily:[di objectForKey:@"isfamily"]]];
        NSLog(@"FlickrPhoto ADDED %@ %lu", [di objectForKey:@"title"], [self.theData count]);
    }
    
    NSLog(@"LETS RELOADDDDDDDDDD");
    
    //thread
	dispatch_queue_t queue = dispatch_get_main_queue();
	dispatch_async(queue, ^{
        [self.collectionView reloadData];
	});
}

@end
