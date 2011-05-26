#! /usr/bin/python

def main():
	filelist = []
	joint_fd = open("bin/combined_test_script.sah", "w")
	list_fd = open("scripts/functional_test.suite", "r")
	for line in list_fd:
		if line.strip() is not "":
			filelist.append(line.strip())	
	list_fd.close()
	for f in filelist:
		s = open("scripts/" + f, "r")
		for line in s:
			joint_fd.write(line)

if __name__ == "__main__":
	main()
