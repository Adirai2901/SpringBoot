package com.bajaj.webhookproject.service;

import com.bajaj.webhookproject.model.User;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowerService {

    public List<Integer> getNthLevelFollowers(int findId, int level, List<User> users) {
        // Build graph from users
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (User user : users) {
            graph.put(user.getId(), user.getFollows());
        }

        // BFS traversal
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(findId);
        visited.add(findId);

        int currentLevel = 0;

        while (!queue.isEmpty() && currentLevel < level) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                List<Integer> nextFollows = graph.getOrDefault(current, Collections.emptyList());
                for (int follower : nextFollows) {
                    if (!visited.contains(follower)) {
                        queue.offer(follower);
                        visited.add(follower);
                    }
                }
            }
            currentLevel++;
        }

        // Remaining in queue are nth-level
        List<Integer> result = new ArrayList<>(queue);
        Collections.sort(result); // optional
        return result;
    }
}
