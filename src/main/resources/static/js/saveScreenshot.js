function getDisplayMedia(options) {
    if (navigator.mediaDevices && navigator.mediaDevices.getDisplayMedia) {
        return navigator.mediaDevices.getDisplayMedia(options)
    }
    if (navigator.getDisplayMedia) {
        return navigator.getDisplayMedia(options)
    }
    if (navigator.webkitGetDisplayMedia) {
        return navigator.webkitGetDisplayMedia(options)
    }
    if (navigator.mozGetDisplayMedia) {
        return navigator.mozGetDisplayMedia(options)
    }
    throw new Error('getDisplayMedia is not defined')
}

function getUserMedia(options) {
    if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        return navigator.mediaDevices.getUserMedia(options)
    }
    if (navigator.getUserMedia) {
        return navigator.getUserMedia(options)
    }
    if (navigator.webkitGetUserMedia) {
        return navigator.webkitGetUserMedia(options)
    }
    if (navigator.mozGetUserMedia) {
        return navigator.mozGetUserMedia(options)
    }
    throw new Error('getUserMedia is not defined')
}

async function takeScreenshotStream() {
    // see: https://developer.mozilla.org/en-US/docs/Web/API/Window/screen
    const width = screen.width * (window.devicePixelRatio || 1)
    const height = screen.height * (window.devicePixelRatio || 1)

    const errors = []
    let stream
    try {
        stream = await getDisplayMedia({
            audio: false,
            // see: https://developer.mozilla.org/en-US/docs/Web/API/MediaStreamConstraints/video
            video: {
                width,
                height,
                frameRate: 1,
            },
        })
    } catch (ex) {
        errors.push(ex)
    }

    // for electron js
    if (navigator.userAgent.indexOf('Electron') >= 0) {
        try {
            stream = await getUserMedia({
                audio: false,
                video: {
                    mandatory: {
                        chromeMediaSource: 'desktop',
                        // chromeMediaSourceId: source.id,
                        minWidth         : width,
                        maxWidth         : width,
                        minHeight        : height,
                        maxHeight        : height,
                    },
                },
            })
        } catch (ex) {
            errors.push(ex)
        }
    }

    if (errors.length) {
        console.debug(...errors)
        if (!stream) {
            throw errors[errors.length - 1]
        }
    }

    return stream
}

async function takeScreenshotCanvas() {
    const stream = await takeScreenshotStream()

    // from: https://stackoverflow.com/a/57665309/5221762
    const video = document.createElement('video')
    const result = await new Promise((resolve, reject) => {
        video.onloadedmetadata = () => {
            video.play()
            video.pause()

            // from: https://github.com/kasprownik/electron-screencapture/blob/master/index.js
            const canvas = document.createElement('canvas')
            canvas.width = video.videoWidth
            canvas.height = video.videoHeight
            const context = canvas.getContext('2d')
            // see: https://developer.mozilla.org/en-US/docs/Web/API/HTMLVideoElement
            context.drawImage(video, 0, 0, video.videoWidth, video.videoHeight)
            resolve(canvas)
        }
        video.srcObject = stream
    })

    stream.getTracks().forEach(function (track) {
        track.stop()
    })
    
    if (result == null) {
        throw new Error('Cannot take canvas screenshot')
    }

    return result
}

// from: https://stackoverflow.com/a/46182044/5221762
function getJpegBlob(canvas) {
    return new Promise((resolve, reject) => {
        // docs: https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement/toBlob
        canvas.toBlob(blob => resolve(blob), 'image/jpeg', 0.95)
    })
}

async function getJpegBytes(canvas) {
    const blob = await getJpegBlob(canvas)
    return new Promise((resolve, reject) => {
        const fileReader = new FileReader()

        fileReader.addEventListener('loadend', function () {
            if (this.error) {
                reject(this.error)
                return
            }
            resolve(this.result)
        })

        fileReader.readAsArrayBuffer(blob)
    })
}

async function takeScreenshotJpegBlob() {
    const canvas = await takeScreenshotCanvas()
    return getJpegBlob(canvas)
}

async function takeScreenshotJpegBytes() {
    const canvas = await takeScreenshotCanvas()
    return getJpegBytes(canvas)
}

function blobToCanvas(blob, maxWidth, maxHeight) {
    return new Promise((resolve, reject) => {
        const img = new Image()
        img.onload = function () {
            const canvas = document.createElement('canvas')
            const scale = Math.min(
                1,
                maxWidth ? maxWidth / img.width : 1,
                maxHeight ? maxHeight / img.height : 1,
            )
            canvas.width = img.width * scale
            canvas.height = img.height * scale
            const ctx = canvas.getContext('2d')
            ctx.drawImage(img, 0, 0, img.width, img.height, 0, 0, canvas.width, canvas.height)
            resolve(canvas)
        }
        img.onerror = () => {
            reject(new Error('Error load blob to Image'))
        }
        img.src = URL.createObjectURL(blob)
    })
}


async function takeScreenshot(){
    // take the screenshot
    var screenshotJpegBlob = await takeScreenshotJpegBlob()
    	
     	
    // show preview with max size 1920 x 1080 px
    var previewCanvas = await blobToCanvas(screenshotJpegBlob, 1920, 1080)
    previewCanvas.style.position = 'fixed'
    document.body.appendChild(previewCanvas)


    // send it to a page of choice
    var formdata = new FormData()
    formdata.append("screenshot", screenshotJpegBlob)
    await fetch(location.origin + '/products/familyTree/saveScreenshot', {
        method: 'POST',
        body: formdata,
        'Content-Type' : "multipart/form-data",
    })
    
    /*	
    //Prompt user to save file - quite slow due to input requirement
    saveAs(screenshotJpegBlob)
    console.log("Immage probably saved - AlexM")
    */	
    
}