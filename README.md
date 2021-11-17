# Phigros Info Collector
_A Java program that crawls information of Phigros from the web._

### What This Program Does
This program visits [the wiki site](https://zh.moegirl.org.cn), from which it crawls information of songs in Phigros.  
Then it converts the information into separate JSON files, which are then respectively put into different song folders.  
Using the information it crawls, we're then able to quickly find chart files for each individual song by checking the number of notes, BPM, etc.

Having sorted them out, we're nowhere far from creating an API for Phigros!

### What We Provide You With
We've hosted a website that serves the information mentioned above.

Feel free to visit it at https://phigros.neonmc.club.

Apart from audio files, illustrations, and the 404 page, all the text information is served in JSON format. That means, it's hardly human-readable, but easy to parse using programs.

By this means, accessing Phigros information becomes easier.

### Usage
As said above, there's no need to clone this repository.  
All you need to do is parse the information that you've got from our site.

Structure of the files:
```
(root)
├── info.json
├── covers
│   ├── chapter-4.png
│   ├── chapter-5-locked.png
│   ├── chapter-5.png
│   ├── chapter-6-locked.png
│   ├── chapter-6.png
│   ├── chapter-7-locked.png
│   ├── chapter-7.png
│   ├── chapter-ex-good.png
│   ├── chapter-ex-hyun.png
│   ├── chapter-ex-jiangmitiao.png
│   ├── chapter-ex-muse-dash.png
│   ├── chapter-ex-rising-sun-traxx.png
│   ├── chapter-ex-waveat.png
│   ├── chapter-legacy.png
│   ├── phigros.png
│   ├── side-story-1.png
│   └── single.png
├── chapter-4
│   ├── info.json
│   ├── bonus-time
│   ├── class-memories
│   ├── energy-synergy-matrix
│   ├── sultan-rage
│   └── surrealism
├── chapter-5
│   ├── info.json
│   ├── cryout
│   ├── junxion-between-life-and-death(vip-mix)
│   └── ...
├── chapter-6
│   └── ...
├── chapter-7
│   └── ...
├── chapter-ex-good
│   └── ...
├── chapter-ex-hyun
│   └── ...
├── chapter-ex-muse-dash
│   └── ...
├── chapter-ex-rising-sun-traxx
│   └── ...
├── chapter-ex-waveat
│   └── ...
├── chapter-legacy
│   └── ...
├── hidden
│   ├── info.json
│   ├── burn(haocore-mix)
│   ├── christmas
│   ├── spasmodic(haocore-mix)
│   └── wintercube-(original-mix)
├── introduction
│   ├── info.json
│   ├── introduction
│   └── introduction-(2.0-ver.)
├── side-story-1
│   ├── info.json
│   ├── lyrith-meikyuuririsu
│   ├── miracle-forest-(vip-mix)
│   └── ...
├── single
│   └── ...
└── unavailable
    ├── info.json
    ├── anomaly
    └── destination
```

##### The Summary File
As shown above, you can first visit the `info.json` in the root directory, whose structure is as the following (adapted):
```json
{
  "version": "2.0.1",
  "last-updated-ts": 1637147959,
  "last-updated": "2021-11-17 19:19:19",
  "cover": "covers/phigros.png",
  "songs": 128,
  "chapters": [
    {
      "name": {
        "subtitle": "Chapter Legacy",
        "title": "过去的章节"
      },
      "direct": true,
      "loc": "chapter-legacy/",
      "cover": "covers/chapter-legacy.png",
      "songs": 15
    },
    {
      "name": {
        "subtitle": "Chapter 5",
        "title": "霓虹灯牌"
      },
      "direct": true,
      "loc": "chapter-5/",
      "cover": "covers/chapter-5.png",
      "cover-locked": "covers/chapter-5-locked.png",
      "songs": 7
    },
    {
      "name": {
        "subtitle": "Side Story 1",
        "title": "忘忧宫"
      },
      "direct": true,
      "loc": "side-story-1/",
      "cover": "covers/side-story-1.png",
      "songs": 5
    },
    {
      "name": {
        "subtitle": "Chapter EX",
        "title": "Rising Sun Traxx 精选集"
      },
      "direct": true,
      "loc": "chapter-ex-rising-sun-traxx/",
      "cover": "covers/chapter-ex-rising-sun-traxx.png",
      "songs": 5
    },
    {
      "name": {
        "subtitle": "Single",
        "title": "单曲集"
      },
      "direct": true,
      "loc": "single/",
      "cover": "covers/single.png",
      "songs": 56
    },
    {
      "name": {
        "subtitle": "Hidden",
        "title": "隐藏曲目"
      },
      "direct": true,
      "loc": "hidden/",
      "songs": 4
    },
    {
      "name": {
        "subtitle": "Introduction",
        "title": "新手教程"
      },
      "direct": true,
      "loc": "introduction/",
      "songs": 2
    },
    {
      "name": {
        "subtitle": "Unavailable",
        "title": "已下架曲目"
      },
      "direct": true,
      "loc": "unavailable/",
      "songs": 2
    },
    {
      "name": {
        "subtitle": "Chapter EX",
        "title": "姜米條 精选集"
      },
      "direct": false,
      "cover": "covers/chapter-ex-jiangmitiao.png",
      "songs": 2
    }
  ]
}
```
Specifically, `version` represents the current version of Phigros of which we provide information, `last-updated-ts` and `last-updated` represent the time when the API has been last updated, respectively in the UNIX timestamp format and a human-readable format.

Then comes the location of _Phigros all-in-one chapter_'s cover (which is `covers/phigros.png`), followed by the total number of songs in Phigros.

In the `chapter` array, there are several JSON objects, each representing one chapter.  
The `name` of a chapter is represented in two strings: `subtitle` and `title`.  
Then the boolean value `direct` represents whether this chapter has its own folder or not. Specifically, if it is true, then there's a folder created for this chapter, where all the songs in the chapter are present, and there'll be a string called `loc` followed by, representing the folder's relative path; otherwise, it means the chapter won't have its own folder.  
If the chapter is available in game, then there'll be a string called `cover` representing the relative path of the cover illustration of the chapter. Moreover, if there's a cover specifically designed for the chapter when its theme song is locked, there's `cover-locked` following after.  
At last, there comes the number of songs in the chapter.

##### The Chapter Info File
The `info.json` in each chapter's folder looks like the following:
```json
{
  "name": {
    "subtitle": "Side Story 1",
    "title": "忘忧宫"
  },
  "songs": [
    {
      "name": "Ποσειδών",
      "loc": "poseidon/"
    },
    {
      "name": "WATER",
      "loc": "water/"
    },
    {
      "name": "Miracle Forest (VIP Mix)",
      "loc": "miracle-forest-(vip-mix)/"
    },
    {
      "name": "MOBILYS",
      "loc": "mobilys/"
    },
    {
      "name": "Lyrith -迷宮リリス-",
      "loc": "lyrith-meikyuuririsu/"
    }
  ]
}
```
The structure is easy to understand, so no further description is provided here.
##### The Song Info File
The `info.json` in each song's folder is formatted as the following:
```json
{
  "name": "もぺもぺ",
  "illustration": "https://zh.moegirl.org.cn/Special:FilePath/Mopemope phi.png",
  "charts": [
    {
      "level": "EZ",
      "difficulty1": 3,
      "difficulty2": 3.4,
      "notes": 125,
      "charter": "阿爽 fixed by Barbarianerman"
    },
    {
      "level": "HD",
      "difficulty1": 8,
      "difficulty2": 8.5,
      "notes": 289,
      "charter": "阿爽 fixed by Barbarianerman"
    },
    {
      "level": "IN",
      "difficulty1": 11,
      "difficulty2": 11.1,
      "notes": 412,
      "charter": "阿爽"
    },
    {
      "level": "AT",
      "difficulty1": 14,
      "difficulty2": 14.9,
      "notes": 720,
      "charter": "阿爽"
    },
    {
      "level": "SP",
      "difficulty1": "?",
      "difficulty2": "?",
      "notes": 720,
      "charter": "阿爽"
    }
  ],
  "chapter": {
    "subtitle": "Single",
    "title": "单曲集"
  },
  "composer": "LeaF",
  "bpm": "100",
  "illustrator": "Cycats",
  "length": "1:50"
}
```
To be specific, the illustration of the song is already in the song's folder with the name of `illustration.png`. The audio file is also available, which is named `music.wav`.
