
# react-native-lubanjs

## Getting started

`$ npm install react-native-lubanjs --save`

### Mostly automatic installation

`$ react-native link react-native-lubanjs`

### Manual installation


#### iOS

1. In XCode, in the project navigator, right click `Libraries` ➜ `Add Files to [your project's name]`
2. Go to `node_modules` ➜ `react-native-lubanjs` and add `RNLubanjs.xcodeproj`
3. In XCode, in the project navigator, select your project. Add `libRNLubanjs.a` to your project's `Build Phases` ➜ `Link Binary With Libraries`
4. Run your project (`Cmd+R`)<

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.dfzn.lubanjs.RNLubanjsPackage;` to the imports at the top of the file
  - Add `new RNLubanjsPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-lubanjs'
  	project(':react-native-lubanjs').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-lubanjs/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-lubanjs')
  	```


## Usage
```javascript
import RNLubanjs from 'react-native-lubanjs';

// TODO: What to do with the module?
RNLubanjs;
```
  