# PermissionX

[![](https://jitpack.io/v/ssseasonnn/PermissionX.svg)](https://jitpack.io/#ssseasonnn/PermissionX)

申请权限也能使用协程？Why not?

## 准备

1. Add jitpack to build.gradle
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2.  Add dependency

```gradle
dependencies {
	implementation 'com.github.ssseasonnn:PermissionX:1.0.0'
}
```

## 使用方法

权限申请只需一行代码

```kotlin
launch {
    try {
        val result = request(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
        if (result.isGranted) {
            //Now we have storage permission.
        }
        if (result.shouldShowRational) {
            //Show permission rational
        }
        if (result.alwaysDenied) {
            //User always denied our permission
        }
    } catch (t: Throwable) {
        Log.w("TAG", t)
    }
}
```

## License

> ```
> Copyright 2019 Season.Zlc
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
