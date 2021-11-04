# Vertical Scroll Picker

Scroll selector like dropdown.</br>
![alt text](https://github.com/redarchid/Vertical-Scroll-Picker/blob/master/app/src/main/res/drawable/sample.png?raw=true)


## Installation
### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### Step 2. Add the dependency [![](https://jitpack.io/v/redarchid/Vertical-Scroll-Picker.svg)](https://jitpack.io/#redarchid/Vertical-Scroll-Picker)

```gradle
dependencies {
	implementation 'com.github.redarchid:Vertical-Scroll-Picker:0.1.0'
}
```
## Usage
Create a `VerticalScrollPicker` in your layout as follows:
```
<com.redarchid.VerticalScrollPicker
	android:id="@+id/verticalScrollPicker"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:setSpacer="2"
        app:markerVisibility="GONE"
        app:textFocusColor="@color/white" 
	/>
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)
