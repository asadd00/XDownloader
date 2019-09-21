# XDownloader

XDownloader is modular library for android apps to provide ease in downloading files or loading images in ImageView. XDownloader can be customizable with various methods.

## Sample Project
This is the [Sample Project](https://github.com/asad-ab/DownloadAnything/tree/master/app) which is using this library


## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Installing

Add this project as modular dependency in your android application.

## Usage

Generally this library can be used in these ways

To load image in ImageView
```
Xdownloader.loadImage(context).load(ImageView, Url)
```

To download any file
```
Xdownloader.downloadFile(context).download(url, fileType)
```

### Customization

```
Xdownloader
	.loadImage(context)
	.placeholder(int)   
	.setCacheEnabled(boolean)
	.setCacheSize(int)
	.load(ImageView, Url)
	
Xdownloader
	.downloadFile(context)
	.setFileName(string)
	.setNotificationEnabled(boolean)
	.setOnDownloadResultListener(OnDownloadResultListener)
	.download(url, fileType)
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Kotlin](http://kotlinlang.org) - Android offical language

## Authors

* **Muhammad Asadullah** - *Whole work* - [PurpleBooth](https://github.com/asad-ab)
