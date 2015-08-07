#!/usr/bin/env python

probDir = './prob/'
probFile = 'prob_13.func'

class phi(object):
    
    probFuncDict = {}
    
    def __init__(self, phiDir, phiFile):
        with open(phiDir + phiFile) as probFunc:
            for line in probFunc:
                (key, val) = line.split()
                self.probFuncDict[int(key)] = float(val)
                
    def calculate(self, utilizaiton):
        utilizaiton = int(utilizaiton)
        if utilizaiton not in self.probFuncDict:
            firstProb = next(iter(self.probFuncDict.values()))
            return firstProb
        return self.probFuncDict[utilizaiton]

def clone_is_valid(func, qps, cloneNum, utilization, qos):
    phiValue = func.calculate(utilization)
    predict_qos = 1 - (1 - phiValue) ** cloneNum
    return predict_qos >= qos

def findLeastCloneNum(func, serverNum, qps, qos):
    cloneNum = 1
    while True:
        utilization = qps * cloneNum / serverNum * 100
        if utilization >= 100:
            return None
        if clone_is_valid(func, qps, cloneNum, utilization, qos):
            return cloneNum
        cloneNum += 1

if __name__ == '__main__':
    func = phi(probDir, probFile)
    serverNum = 150
    qps = 37
    qos = .999
    leastCloneNum = findLeastCloneNum(func, serverNum, qps, qos)
    print(leastCloneNum)