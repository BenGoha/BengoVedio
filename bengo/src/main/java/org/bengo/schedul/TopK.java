package org.bengo.schedul;

import org.bengo.entity.video.Video;
import org.bengo.entity.vo.HotVideo;

import java.util.*;

/**
*
 * @description:
 * @author wuGuanRen
*/
public class TopK {

    private int k = 0;

    private final Queue<HotVideo> queue;

    public TopK(int k,Queue<HotVideo> queue){
        this.k = k;
        this.queue = queue;
    }

    public void add(HotVideo hotVideo) {
        if (queue.size() < k) {
            queue.add(hotVideo);
        } else if (queue.peek().getHot() < hotVideo.getHot()){
            queue.poll();
            queue.add(hotVideo);
        }

    }


    public List<HotVideo> get(){
        final ArrayList<HotVideo> list = new ArrayList<>(queue.size());
        while (!queue.isEmpty()) {
            list.add(queue.poll());
        }
        Collections.reverse(list);
        return list;
    }


}
