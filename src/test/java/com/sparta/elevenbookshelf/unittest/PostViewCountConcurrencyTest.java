package com.sparta.elevenbookshelf.unittest;

import com.sparta.elevenbookshelf.domain.post.entity.Post;
import com.sparta.elevenbookshelf.domain.post.repository.PostRepository;
import com.sparta.elevenbookshelf.domain.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@TestPropertySource(locations = "file:./.env")
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostViewCountConcurrencyTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    private Post post;

    @Test
    public void testViewCountOptimisticLocking() throws InterruptedException {
        int numberOfThreads = 5;
        int viewsPerThread = 20;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < viewsPerThread; j++) {
                        postService.readPost(3L);
                        System.out.printf("Thread %s - View count incremented%n", Thread.currentThread().getName());
                    }
                } catch (Exception e) {
                    System.out.printf("Error in thread %s: %s%n", Thread.currentThread().getName(), e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Post updatedPost = postRepository.findById(3L).orElseThrow();
        System.out.printf("Final view count: %d%n", updatedPost.getViewCount());

        assertEquals(numberOfThreads * viewsPerThread, updatedPost.getViewCount());

        executorService.shutdown();
    }

}
