<template>
    <v-card
        ref="cardRef"
        v-if="props.videoInfo"
        hover
        ripple
        :elevation="0"
        rounded="lg"
        class="video-card"
    >
        <v-img
            :src="props.videoInfo.cover ? apiFileGet(props.videoInfo.cover) : '/not-found.png'"
            class="align-end"
            gradient="to bottom, rgba(0,0,0,.1), rgba(0,0,0,.5)"
            :height="$vuetify.display.smAndDown ? '200px' : '300px'"
            cover
        >
            <v-card-text class="text-white pa-0" v-if="!overlay">
                <v-card-actions class="ml-1 mr-1 pa-0">
          <span class="ma-2 font-weight-bold">
            <v-icon size="small">mdi-heart</v-icon>
            {{ formatCount(props.videoInfo.startCount) }} 点赞
          </span>
                    <v-spacer />
                    <v-btn :variant="'tonal'" :density="'compact'" size="small">
                        {{ props.videoInfo.duration }}
                    </v-btn>
                </v-card-actions>
            </v-card-text>
        </v-img>

        <v-card-actions class="pa-2">
      <span class="video-title text-truncate">
        {{ props.videoInfo.title }}
      </span>
            <v-spacer />
            <v-btn
                v-if="props.videoInfo.user"
                size="small"
                color="white"
                variant="tonal"
                :to="userStore.token ? `/user?lookId=${props.videoInfo.user.id}` : ''"
                @click.stop
            >
                @{{ props.videoInfo.user.nickName }}
            </v-btn>
        </v-card-actions>

        <v-overlay
            scrim="black"
            :model-value="overlay"
            contained
            persistent
            class="overlay-content"
        >
            <v-card color="rgba(1,1,1,0.5)" :height="$vuetify.display.smAndDown ? 'auto' : '350px'">
                <v-card-title class="pb-0 text-h6">播放中</v-card-title>
                <v-chip-group class="ml-2 mr-2" column>
                    <v-chip
                        v-for="item in props.videoInfo.labelNames.split(',')"
                        :key="item"
                        size="small"
                    >
                        {{ item }}
                    </v-chip>
                </v-chip-group>
                <v-card-subtitle class="mt-2">
                    <v-row dense>
                        <v-col cols="4" class="text-center">
                            {{ formatCount(props.videoInfo.historyCount) }} 播放
                        </v-col>
                        <v-col cols="4" class="text-center">
                            {{ formatCount(props.videoInfo.startCount) }} 点赞
                        </v-col>
                        <v-col cols="4" class="text-center">
                            {{ formatCount(props.videoInfo.favoritesCount || 0) }} 收藏
                        </v-col>
                    </v-row>
                </v-card-subtitle>

                <v-card-actions class="pb-0 pt-0">
                    <v-chip variant="plain" :density="'compact'" @click="copyUrl()">
                        YV: {{ props.videoInfo.yv }}
                    </v-chip>
                    <v-chip color="orange-lighten-2" variant="text" @click="showDescription = !showDescription">
                        描述
                    </v-chip>
                    <v-spacer />
                </v-card-actions>

                <v-expand-transition>
                    <div v-show="showDescription">
                        <v-divider />
                        <v-card-text class="description-text">
                            {{ props.videoInfo.description || "作者很懒，没有给一点描述" }}
                        </v-card-text>
                    </div>
                </v-expand-transition>
            </v-card>
        </v-overlay>

        <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="2000">
            {{ snackbar.text }}
            <template v-slot:actions>
                <v-btn color="blue" variant="text" @click="snackbar.show = false">
                    关闭
                </v-btn>
            </template>
        </v-snackbar>
    </v-card>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { apiFileGet } from '../../apis/file';
import { useUserStore } from '../../stores';
import strUtils from '../../utils/strUtil';

const showDescription = ref(false);
const userStore = useUserStore();
const cardRef = ref();
const snackbar = ref({
    show: false,
    text: "",
    color: "success",
});

const props = defineProps({
    videoInfo: {
        type: Object,
        default: null,
    },
    overlay: {
        type: Boolean,
        default: false,
    },
});

// 格式化数字（超过1万显示为“x.x万”）
const formatCount = (count) => {
    if (count >= 10000) {
        return `${(count / 10000).toFixed(1)}万`;
    }
    return count;
};

const copyUrl = () => {
    const success = strUtils.copyContent(props.videoInfo.yv);
    snackbar.value = {
        text: success ? "视频YV号复制成功" : "视频YV号复制失败",
        show: true,
        color: success ? "success" : "error",
    };
};

onMounted(() => {
    showDescription.value = props.overlay;
});

watch(
    () => props.overlay,
    (newVal) => {
        showDescription.value = newVal;
        if (newVal && cardRef.value) {
            cardRef.value.$el.scrollIntoView({ behavior: "smooth", block: "center" });
        }
    }
);
</script>

<style scoped>
.video-card {
    width: 100%;
    max-width: 400px; /* 限制最大宽度，适合手机端 */
    margin: 0 auto;
}

.video-title {
    max-width: 70%;
    color: white;
    font-size: 1rem;
}

.description-text {
    font-size: 0.9rem;
    line-height: 1.4;
    max-height: 100px;
    overflow-y: auto;
}

/* 响应式调整 */
@media (max-width: 600px) {
    .video-card {
        max-width: 100%;
    }

    .video-title {
        font-size: 0.9rem;
        max-width: 60%;
    }

    .overlay-content .v-card {
        height: auto; /* 移动端自适应高度 */
        max-height: 80vh;
        overflow-y: auto;
    }

    .v-chip-group {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
    }

    .v-btn {
        font-size: 0.8rem;
    }
}

@media (min-width: 601px) {
    .video-card {
        max-width: 350px;
    }
}
</style>