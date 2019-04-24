
# react-native-lubanjs

## Getting started

`$ npm install react-native-lubanjs --save`

### Mostly automatic installation

`$ react-native link react-native-lubanjs`

### Manual installation
# git
 https://github.com/EMail2HF/lubanjs

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
  

## Example 1

```javascript

/**
 * Sample React Native App
 * https://github.com/EMail2HF/lubanjs
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TouchableOpacity } from 'react-native';
import RNLubanjs from 'react-native-lubanjs';
import ImagePicker from 'react-native-image-crop-picker';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
    super(props);
    this.state = {
      filepath: '',
      outfile: '',
      selectedImages:[] ,
    }
  }

  componentDidMount() {
 
  }

  onProcessStatus = (result,content)=>{

	  if(result=='OK'){
		  //SUCESSED
	  }
	  else{
		  //Failed
	  }

    console.info(result);

    console.info(content);
  }

  processImage = (filepath) => {
 
    let filelist = [filepath.path]; 
    let options = {filepath:filepath.path,targetdir:'/com.apual.lubanjs/compress/images/'};
    console.info(filepath.path);
    RNLubanjs.Compress(options,this.onProcessStatus);
  }

  pickImage() { 
    if (this.state.selectedImages.length >= 9) { 
      return;
    }
    ImagePicker.openPicker({
      width: 300,
      height: 300,
      cropping: false,
      multiple: Platform.OS == 'ios' ? true : false,
    }).then(image => {
      let arr = [];
      //arr.push(image.path);
      if (Platform.OS == 'ios') {
        for (let i = 0; i < image.length; i++) {
          arr.push({ type: 'image', path: image[i].path });
        }
      } else {
        arr.push({ type: 'image', path: image.path });
      }

      this.setState({ selectedImages: arr });


      if(arr.length>0){

        this.processImage(arr[0]);
      }

    });
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <TouchableOpacity onPress={() => {
          this.pickImage();
        }}>
          <Text style={styles.instructions}>select Image </Text>
        </TouchableOpacity>

        <Text style={styles.instructions}>{instructions}</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});


```

## Example 2

```javascript
/**
 * Sample React Native App
 * https://github.com/EMail2HF/lubanjs
 *
 * @format
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, TouchableOpacity, DeviceEventEmitter } from 'react-native';
import RNLubanjs from 'react-native-lubanjs';
import ImagePicker from 'react-native-image-crop-picker';

const instructions = Platform.select({
  ios: 'Press Cmd+R to reload,\n' + 'Cmd+D or shake for dev menu',
  android:
    'Double tap R on your keyboard to reload,\n' +
    'Shake or press menu button for dev menu',
});

type Props = {};
export default class App extends Component<Props> {

  constructor(props) {
    super(props);
    this.state = {
      filepath: '',
      outfile: '',
      selectedImages: [],
    }
  }

  componentWillMount() {

    this.subscription = DeviceEventEmitter.addListener('lubanjs-event', function (msg) {
      console.log(msg);

      if(msg.status=="begin"){    //
      }
      else if(msg.status=="finished"){
        //
        let filearray = msg.content;
      }
      else{
        //failed
      }
    });
  }
 

  componentWillUnmount() {

    this.subscription.remove();

  }

 

  processImage = () => { 
   try {

      let images = this.state.selectedImages;

      let filelist = images.map(item => {
        return item.path;
      })
      if (filelist.length > 0) {
        let options = { filelist: filelist, targetdir: '/lubanjs/compress/images/' };

        RNLubanjs.CompressWithNotify(options);
      }

    } catch (error) {
      console.info(error)
    }
  }

  

  pickImage() {
    //this.props.navigation.navigate('TestCameraKitScene');
    if (this.state.selectedImages.length >= 9) {
      //Toast.showShortCenter('最多只能添加9张图片');
      return;
    }
    ImagePicker.openPicker({
      width: 300,
      height: 300,
      cropping: false,
      multiple: Platform.OS == 'ios' ? true : false,
    }).then(image => {
      let arr = [];
      //arr.push(image.path);
      if (Platform.OS == 'ios') {
        for (let i = 0; i < image.length; i++) {
          arr.push({ type: 'image', path: image[i].path });
        }
      } else {
        arr.push({ type: 'image', path: image.path });
      }

      this.setState({ selectedImages: arr });


      if (arr.length > 0) {

        this.processImage();
      }

    });
  }
  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <TouchableOpacity onPress={() => {
          this.pickImage();
        }}>
          <Text style={styles.instructions}>select Image </Text>
        </TouchableOpacity>

        <Text style={styles.instructions}>{instructions}</Text>
      </View>
    );
  }
}

 

```
