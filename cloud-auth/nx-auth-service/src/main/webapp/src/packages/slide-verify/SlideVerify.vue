<template>
  <div
    class="slide-verify"
    :style="{width: w + 'px'}"
    id="slideVerify"
    onselectstart="return false;"
  >
    <!-- container begin -->
    <!-- 图片加载遮蔽罩 -->

    <div :class="{'slider-verify-loading': loadBlock}"></div>
    <canvas
      :width="w"
      :height="h"
      ref="canvas"
    ></canvas>
    <div
      @click="refresh"
      class="slide-verify-refresh-icon"
    ></div>
    <canvas
      :width="w"
      :height="h"
      ref="block"
      class="slide-verify-block"
    ></canvas>
    <!-- container end -->

    <!-- 滑动条 -->
    <div
      class="slide-verify-slider"
      :class="{'container-active': containerActive, 'container-success': containerSuccess, 'container-fail': containerFail}"
    >
      <div class="slide-verify-slider-mask" :style="{width: sliderMaskWidth}"  >
        <div
          @mousedown="sliderDown"
          @touchstart="touchStartEvent"
          @touchmove="touchMoveEvent"
          @touchend="touchEndEvent"
          class="slide-verify-slider-mask-item"
          :style="{left: sliderLeft}"
        >
          <div class="slide-verify-slider-mask-item-icon" ></div>
        </div>  <!-- slider(箭头按钮) -->
      </div>
      <span class="slide-verify-slider-text">{{sliderText}}</span>
    </div>
  </div>
</template>
<script  lang="ts">
import {defineComponent, reactive, toRefs, onMounted, ref, watch} from "vue";
import {SuccessData, VerifyType, vertifyUtils} from "@/packages/slide-verify/vertify-utils";


const PI = Math.PI;

function sum(x: number, y: number): number {
  return x + y;
}

function square(x: number): number {
  return x * x;
}

const  position = vertifyUtils.getDefaultValue();

export default defineComponent({
  name: "SlideVerify",
  props: {
    // block length
    l: {
      type: Number,
      default: position.l,
    },
    // block radius
    r: {
      type: Number,
      default: position.r,
    },
    // canvas width
    w: {
      type: Number,
      default: position.w,
    },
    // canvas height
    h: {
      type: Number,
      default: position.h,
    },
    sliderText: {
      type: String,
      default: "向右滑动",
    },
    show: {
      type: Boolean,
      default: true,
    },
    accuracy: {
      type: Number,
      default: position.accuracy, // 若为 -1 则不进行机器判断
    },
    imgs: {
      type: Array,
      default: () => [],
    },
    blockPosition: {
      type: vertifyUtils.BlockPosition,
      default: null,
    }
  },
  emits: ["success", "refresh", "again", "fail", "fulfilled"],
  setup(props, { emit }) {
    const data = reactive({
      containerActive: <boolean>false, // container active class
      containerSuccess: <boolean>false, // container success class
      containerFail: <boolean>false, // container fail class
      canvas: <HTMLCanvasElement | null>null,
      canvasCtx: <CanvasRenderingContext2D | null>null,
      blockCtx: <CanvasRenderingContext2D | null>null,
      block: <HTMLCanvasElement | null>null,
      block_x: <number>0, // container random position
      block_y: <number>0,
      L: <number>props.l + props.r * 2 + 3, // block real lenght
      img: <HTMLImageElement | null>null,
      imgList: <string[]>[],
      originX: <number>0,
      originY: <number>0,
      isMouseDown: <boolean>false,
      trail: <number[]>[],
      sliderLeft: <string>"0px", // block right offset
      sliderMaskWidth: <string>"0px", // mask width,
      success: <boolean>false, // Bug Fixes 修复了验证成功后还能滑动
      loadBlock: <boolean>true, // Features 图片加载提示，防止图片没加载完就开始验证
      timestamp: <number>0,

    });


    const exposeInfo = {verifyType:VerifyType.default}; //从父窗体中得到动态数据
    const setVerifyType = (type:VerifyType):void => {
      exposeInfo.verifyType = type;
      reset();
    }

    onMounted(() => {
      initDom();
      initImg();
      bindEvents();
    });

    const initDom = () => {
      data.canvasCtx = data.canvas.getContext("2d");
      data.blockCtx = data.block.getContext("2d");
    };

    const initImgList = () => {
      interface ImgFiles {
        [key: string]: any;
      }

      const files: ImgFiles = import.meta.globEager("./assets/default/*.jpg");
      const images: string[] = [];

      for (const key in files) {
        if (Object.prototype.hasOwnProperty.call(files, key)) {
          images.push((<ImgFiles>files)[key].default);
        }
      }

      data.imgList = images;
    };

    const initImg = () => {
      const img = createImg(() => {
        // 图片加载完关闭遮蔽罩
        data.loadBlock = false;


        //drawBlock();
        //drawBlock()-begin
        vertifyUtils.getRemoteBlockPosition(data.L,props.r, props.w,props.h,exposeInfo.verifyType).then((blockPosition)=> {
          console.log("2.返回值:" + JSON.stringify(blockPosition));
          if (blockPosition && blockPosition != null) {
            data.block_x = blockPosition.blockX;
            data.block_y = blockPosition.blockY;
            console.log("3.data.block_x="+data.block_x+"---data.block_y"+data.block_y)
          } else {
            data.block_x = vertifyUtils.getRandomNumberByRange(
                data.L + 10,
                props.w - (data.L + 10)
            );
            data.block_y = vertifyUtils.getRandomNumberByRange(
                10 + props.r * 2,
                props.h - (data.L + 10)
            );
          }
          draw(data.canvasCtx, data.block_x, data.block_y, "fill");
          draw(data.blockCtx, data.block_x, data.block_y, "clip");


          data.canvasCtx.drawImage(img, 0, 0, props.w, props.h);
          data.blockCtx.drawImage(img, 0, 0, props.w, props.h);

          let _y = data.block_y - props.r * 2 - 1;
          let ImageData = data.blockCtx.getImageData(
              data.block_x,
              _y,
              data.L,
              data.L
          );
          data.block.width = data.L;
          data.blockCtx.putImageData(ImageData, 0, _y);
        });
      });
      data.img = img;
    };

    // const drawBlock = (): void => {
    //   getRemoteBlockPosition(data.L,props.r, props.w,props.h,exposeInfo.verifyType).then((blockPosition)=> {
    //     console.log("2.返回值:" + JSON.stringify(blockPosition));
    //     if (blockPosition && blockPosition != null) {
    //       data.block_x = blockPosition.blockX;
    //       data.block_y = blockPosition.blockY;
    //       console.log("3.data.block_x="+data.block_x+"---data.block_y"+data.block_y)
    //     } else {
    //       data.block_x = getRandomNumberByRange(
    //           data.L + 10,
    //           props.w - (data.L + 10)
    //       );
    //       data.block_y = getRandomNumberByRange(
    //           10 + props.r * 2,
    //           props.h - (data.L + 10)
    //       );
    //     }
    //     draw(data.canvasCtx, data.block_x, data.block_y, "fill");
    //     draw(data.blockCtx, data.block_x, data.block_y, "clip");
    //   });
    // };


    const getRandomImg = () => {
      let imgLength = props.imgs.length;
      let imgArray = props.imgs;
      if (imgLength <= 0) {
        if (data.imgList.length <= 0) {
          initImgList();
        }

        imgLength = data.imgList.length;
        imgArray = data.imgList;
      }

      return imgArray[vertifyUtils.getRandomNumberByRange(0, imgLength)];

    };

    const createImg = function (onload: any) {
      const img: HTMLImageElement = document.createElement("img");
      img.crossOrigin = "Anonymous";
      img.onload = onload;
      img.onerror = () => {
        img.src = getRandomImg();
      };
      img.src = getRandomImg();

      return img;
    };

    const draw = (
      ctx: CanvasRenderingContext2D,
      x: number,
      y: number,
      operation: string
    ): void => {


      const l = props.l;
      const r = props.r;



        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.arc(x + l / 2, y - r + 2, r, 0.72 * PI, 2.26 * PI);
        ctx.lineTo(x + l, y);
        ctx.arc(x + l + r - 2, y + l / 2, r, 1.21 * PI, 2.78 * PI);
        ctx.lineTo(x + l, y + l);
        ctx.lineTo(x, y + l);
        ctx.arc(x + r - 2, y + l / 2, r + 0.4, 2.76 * PI, 1.24 * PI, true);
        ctx.lineTo(x, y);
        ctx.lineWidth = 2;
        ctx.fillStyle = "rgba(255, 255, 255, 0.7)";
        ctx.strokeStyle = "rgba(255, 255, 255, 0.7)";
        ctx.stroke();

        if (operation === "fill") {
          ctx.fill();
        } else if (operation === "clip") {
          ctx.clip();
        }
        // Bug Fixes 修复了火狐和ie显示问题
        ctx.globalCompositeOperation = "destination-over";


    };

    const refresh = (): void => {
      reset();
      emit("refresh");
    };

    const sliderDown = (event: MouseEvent): void => {
      if (data.success) return;
      data.originX = event.clientX;
      data.originY = event.clientY;
      data.isMouseDown = true;
      data.timestamp = +new Date();
    };
    const touchStartEvent = (e: TouchEvent): void => {
      if (data.success) return;
      data.originX = e.changedTouches[0].pageX;
      data.originY = e.changedTouches[0].pageY;
      data.isMouseDown = true;
      data.timestamp = +new Date();
    };


    const bindEvents = (): void => {
      document.addEventListener("mousemove", (e: MouseEvent) => {
        if (!data.isMouseDown) return false;
        const moveX = e.clientX - data.originX;
        const moveY = e.clientY - data.originY;
        if (moveX < 0 || moveX + 38 >= props.w) return false;

        data.sliderLeft = moveX + "px";

        let blockLeft = ((props.w - 42 - 20) / (props.w - 42)) * moveX;

        data.block.style.left = blockLeft + "px";

        data.containerActive = true; // add active
        data.sliderMaskWidth = moveX + "px";
        data.trail.push(moveY);
      });

      document.addEventListener("mouseup", (e: MouseEvent) => {
        if (!data.isMouseDown) return false;
        data.isMouseDown = false;
        if (e.clientX === data.originX) return false;
        data.containerActive = false; // remove active
        data.timestamp = +new Date() - data.timestamp;

        const { spliced, TuringTest, blockLeft, blockAccuracy } = verify();

        if (spliced) {
          const successData:SuccessData = new SuccessData(data.timestamp,blockLeft,blockAccuracy,exposeInfo.verifyType)
          //   timestamp: data.timestamp,
          //   left: blockLeft,
          //   accuracy: blockAccuracy,
          //   verifyType: exposeInfo.verifyType
          // }

          if (props.accuracy === -1) {
            data.containerSuccess = true;
            data.success = true;
            emit("success", successData);
            return;
          }
          if (TuringTest) {
            // succ
            data.containerSuccess = true;
            data.success = true;

            // const successData:SuccessData = {
            //   timestamp: data.timestamp,
            //   left: blockLeft,
            //   accuracy: blockAccuracy,
            //   verifyType: exposeInfo.verifyType
            // }

            emit("success", successData);
          } else {
            data.containerFail = true;
            emit("again");
          }
        } else {
          data.containerFail = true;
          emit("fail");
          setTimeout(() => {
            reset();
          }, 1000);
        }
      });
    };

    const touchMoveEvent = (e: TouchEvent): boolean => {
      if (!data.isMouseDown) return false;
      const moveX = e.changedTouches[0].pageX - data.originX;
      const moveY = e.changedTouches[0].pageY - data.originY;
      if (moveX < 0 || moveX + 38 >= props.w) return false;
      data.sliderLeft = moveX + "px";
      let blockLeft = ((props.w - 40 - 20) / (props.w - 40)) * moveX;
      data.block.style.left = blockLeft + "px";

      data.containerActive = true;
      data.sliderMaskWidth = moveX + "px";
      data.trail.push(moveY);

      return true;
    };

    const touchEndEvent = (e: TouchEvent): boolean => {
      if (!data.isMouseDown) return false;
      data.isMouseDown = false;
      if (e.changedTouches[0].pageX === data.originX) return false;
      data.containerActive = false;
      data.timestamp = + new Date() - data.timestamp

      const { blockLeft, blockAccuracy, spliced, TuringTest } = verify();
      if (spliced) {
        if (props.accuracy === -1) {
          data.containerSuccess = true;
          data.success = true;
          emit("success", {
            timestamp: data.timestamp,
            left: blockLeft,
            accuracy: blockAccuracy,
            verifyType: exposeInfo.verifyType,
          });
          return false;
        }
        if (TuringTest) {
          // succ
          data.containerSuccess = true;
          data.success = true;
          emit("success", {
            timestamp: data.timestamp,
            left: blockLeft,
            accuracy: blockAccuracy,
            verifyType: exposeInfo.verifyType,
          });
        } else {
          data.containerFail = true;
          emit("again");
        }

        return true;
      } else {
        data.containerFail = true;
        emit("fail");
        setTimeout(() => {
          reset();
        }, 1000);

        return false;
      }
    };

    const verify = () => {
      const arr = data.trail; // drag y move distance
      const average = arr.reduce(sum) / arr.length; // average
      const deviations = arr.map((x) => x - average); // deviation array
      const stddev = Math.sqrt(deviations.map(square).reduce(sum) / arr.length); // standard deviation
      const left = parseInt(data.block.style.left);
      const accuracy =
        props.accuracy <= 1 ? 1 : props.accuracy > 10 ? 10 : props.accuracy;
      return {
        blockLeft: <number>left,
        blockAccuracy: <number>accuracy,
        spliced: <boolean>(Math.abs(left - data.block_x) <= accuracy),
        TuringTest: <boolean>(average !== stddev), // equal => not person operate
      };
    };

    const reset = (): void => {
      data.success = false;
      data.containerActive = false;
      data.containerSuccess = false;
      data.containerFail = false;
      data.sliderLeft = "0px";
      data.block.style.left = "0";
      data.sliderMaskWidth = "0px";

      let w = props.w;
      let h = props.h;

      data.canvasCtx.clearRect(0, 0, w, h);
      data.blockCtx.clearRect(0, 0, w, h);
      data.block.width = w;


      // generate img
      data.img.src = getRandomImg();
      emit("fulfilled");
    };

    const dataAsRefs = toRefs(data);

    return {
      ...dataAsRefs,
      sliderDown,
      refresh,
      touchStartEvent,
      touchMoveEvent,
      touchEndEvent,
      setVerifyType,
    };


    defineExpose({
      setVerifyType
    })
  },

});
</script>
<style scoped>
.slide-verify {
  position: relative;
}

/* 图片加载样式 */
.slider-verify-loading {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  z-index: 999;
  animation: loading 1.5s infinite;
}

@keyframes loading {
  0% {
    opacity: 0.7;
  }
  100% {
    opacity: 9;
  }
}

.slide-verify-block {
  position: absolute;
  left: 0;
  top: 0;
}

.slide-verify-refresh-icon {
  position: absolute;
  right: 0;
  top: 0;
  width: 34px;
  height: 34px;
  cursor: pointer;
  background: url("assets/icon_light.png") 0 -437px;
  background-size: 34px 471px;
}

.slide-verify-slider {
  position: relative;
  text-align: center;
  width: 100%;
  height: 40px;
  line-height: 40px;
  margin-top: 15px;
  background: #f7f9fa;
  color: #45494c;
  border: 1px solid #e4e7eb;
}

.slide-verify-slider-mask {
  position: absolute;
  left: 0;
  top: 0;
  height: 40px;
  border: 0 solid #1991fa;
  background: #d1e9fe;
}

.slide-verify-slider-mask-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 40px;
  height: 40px;
  background: #fff;
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.3);
  cursor: pointer;
  transition: background 0.2s linear;
}

.slide-verify-slider-mask-item:hover {
  background: #1991fa;
}

.slide-verify-slider-mask-item:hover .slide-verify-slider-mask-item-icon {
  background-position: 0 -13px;
}

.slide-verify-slider-mask-item-icon {
  position: absolute;
  top: 15px;
  left: 13px;
  width: 14px;
  height: 12px;
  background: url("assets/icon_light.png") 0 -26px;
  background-size: 34px 471px;
}
.container-active .slide-verify-slider-mask-item {
  height: 38px;
  top: -1px;
  border: 1px solid #1991fa;
}

.container-active .slide-verify-slider-mask {
  height: 38px;
  border-width: 1px;
}

.container-success .slide-verify-slider-mask-item {
  height: 38px;
  top: -1px;
  border: 1px solid #52ccba;
  background-color: #52ccba !important;
}

.container-success .slide-verify-slider-mask {
  height: 38px;
  border: 1px solid #52ccba;
  background-color: #d2f4ef;
}

.container-success .slide-verify-slider-mask-item-icon {
  background-position: 0 0 !important;
}

.container-fail .slide-verify-slider-mask-item {
  height: 38px;
  top: -1px;
  border: 1px solid #f57a7a;
  background-color: #f57a7a !important;
}

.container-fail .slide-verify-slider-mask {
  height: 38px;
  border: 1px solid #f57a7a;
  background-color: #fce1e1;
}

.container-fail .slide-verify-slider-mask-item-icon {
  top: 14px;
  background-position: 0 -82px !important;
}

.container-active .slide-verify-slider-text,
.container-success .slide-verify-slider-text,
.container-fail .slide-verify-slider-text {
  display: none;
}
</style>
