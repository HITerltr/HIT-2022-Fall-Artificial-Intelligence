# search.py
# ---------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


"""
In search.py, you will implement generic search algorithms which are called by
Pacman agents (in searchAgents.py).
"""

import util

class SearchProblem:
    """
    This class outlines the structure of a search problem, but doesn't implement
    any of the methods (in object-oriented terminology: an abstract class).

    You do not need to change anything in this class, ever.
    """

    def getStartState(self):
        """
        Returns the start state for the search problem.
        """
        util.raiseNotDefined()

    def isGoalState(self, state):
        """
          state: Search state

        Returns True if and only if the state is a valid goal state.
        """
        util.raiseNotDefined()

    def expand(self, state):
        """
          state: Search state

        For a given state, this should return a list of triples, (child,
        action, stepCost), where 'child' is a child to the current
        state, 'action' is the action required to get there, and 'stepCost' is
        the incremental cost of expanding to that child.
        """
        util.raiseNotDefined()

    def getActions(self, state):
        """
          state: Search state

        For a given state, this should return a list of possible actions.
        """
        util.raiseNotDefined()

    def getActionCost(self, state, action, next_state):
        """
          state: Search state
          action: action taken at state.
          next_state: next Search state after taking action.

        For a given state, this should return the cost of the (s, a, s') transition.
        """
        util.raiseNotDefined()

    def getNextState(self, state, action):
        """
          state: Search state
          action: action taken at state

        For a given state, this should return the next state after taking action from state.
        """
        util.raiseNotDefined()

    def getCostOfActionSequence(self, actions):
        """
         actions: A list of actions to take

        This method returns the total cost of a particular sequence of actions.
        The sequence must be composed of legal moves.
        """
        util.raiseNotDefined()


def tinyMazeSearch(problem):
    """
    Returns a sequence of moves that solves tinyMaze.  For any other maze, the
    sequence of moves will be incorrect, so only use this for tinyMaze.
    """
    from game import Directions
    s = Directions.SOUTH
    w = Directions.WEST
    return  [s, s, w, s, w, w, s, w]

def depthFirstSearch(problem):
    """
    Search the deepest nodes in the search tree first.

    Your search algorithm needs to return a list of actions that reaches the
    goal. Make sure to implement a graph search algorithm.

    To get started, you might want to try some of these simple commands to
    understand the search problem that is being passed in:

    print("Start:", problem.getStartState())
    print("Is the start a goal?", problem.isGoalState(problem.getStartState()))
    """
    actual_position = (problem.getStartState(), [])#设置该变量前一处存放节点的起始位置，后一处存放遍历路径
    upcoming_nodes = util.Stack()#未访问的节点使用栈这种有先进后出特点的数据结构来记录
    visited_nodes = set()#该集合存放并记录已经访问过的节点
    while not problem.isGoalState(actual_position[0]):#判断：如果当前位置不是目标节点，那么就开始循环
        if actual_position[0] not in visited_nodes: 
            visited_nodes.add(actual_position[0])#首先把当前位置加入到已经访问过的记录表内
            successors = problem.expand(actual_position[0])#expand函数共返回3个状态值：子节点，遍历动作方向，每一步所耗的费用
            for successor in successors:
                new = successor[0]
                new_1 = successor[1]
                if new not in visited_nodes:#判断如果子节点不在已经访问过的节点中，那么就压栈新的节点
                    upcoming_nodes.push((new, actual_position[1] + [new_1]))
        actual_position = upcoming_nodes.pop()
        #print("actual:", actual_position)
    return actual_position[1]
    #util.raiseNotDefined()

def breadthFirstSearch(problem):
    """Search the shallowest nodes in the search tree first."""
    actual_position = (problem.getStartState(), [])
    upcoming_nodes = util.Queue()#与深度优先搜索相比，将使用的数据结构从栈改为队列，利用其先进先出的特点，其余均相同
    visited_nodes = set()
    while not problem.isGoalState(actual_position[0]):
        if actual_position[0] not in visited_nodes:
            visited_nodes.add(actual_position[0])
            successors = problem.expand(actual_position[0])  
            for successor in successors:
                new = successor[0]
                new_1 = successor[1]
                if new not in visited_nodes:
                    upcoming_nodes.push((new, actual_position[1] + [new_1]))
        actual_position = upcoming_nodes.pop()
        #print("actual:", actual_position)
    return actual_position[1]
    #util.raiseNotDefined()

def nullHeuristic(state, problem=None):
    """
    A heuristic function estimates the cost from the current state to the nearest
    goal in the provided SearchProblem.  This heuristic is trivial.
    """
    return 0

def aStarSearch(problem, heuristic=nullHeuristic):
    actual_position = (problem.getStartState(), [], 0)
    upcoming_nodes = util.PriorityQueue()#使用小顶堆结构（优先队列）来实现状态的排列
    visited_nodes = set()
    while not problem.isGoalState(actual_position[0]):
        if actual_position[0] not in visited_nodes:
            visited_nodes.add(actual_position[0])
            successors = problem.expand(actual_position[0])#expand函数共返回3个状态值：子节点，遍历动作方向，每一步所耗的费用
            for successor in successors:#循环取堆顶状态，直到找到目标状态
                new = successor[0]
                new_1 = successor[1]
                if new not in visited_nodes:
                    cost = actual_position[2] + successor[2]#子节点加入堆时的权重为：从初始状态到父节点状态的代价+父节点状态到子节点状态的代价+heuristic(successor)
                    upcoming_nodes.push(
                        (new, actual_position[1] + [new_1], cost),
                        cost + heuristic(new, problem)
                    )
        actual_position =  upcoming_nodes.pop()
        #print("actual:", actual_position)
    return actual_position[1]
    #util.raiseNotDefined()


# Abbreviations
bfs = breadthFirstSearch
dfs = depthFirstSearch
astar = aStarSearch
