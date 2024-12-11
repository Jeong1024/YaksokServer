import cv2
import os
import errno
import numpy as np


class ImageFiltering():
    def __init__(self, config):
        '''
        rotate_path : rotate image save path
        filter_path : filter image save path
        '''
        
        self.origin_folder_path = config['rotate_path']
        self.filter_folder_path = config['filter_path']
        
        # Permission Error retry another path
        self.error_path_filter = './filter'


    # make white background
    def white_Background(self, img):
        if img.shape[2] == 4:
            trans_mask = img[:, :, 3] == 0
            img[trans_mask] = [255, 255, 255, 255]
            img = cv2.cvtColor(img, cv2.COLOR_BGRA2BGR)

        return img


    # filter
    def max_con_CLAHE(self, img):
        # Converting image to LAB Color model
        lab = cv2.cvtColor(img, cv2.COLOR_BGR2LAB)
        l, a, b = cv2.split(lab)
    
        # Applying CLAHE to L-channel
        clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8,8))
        cl = clahe.apply(l)
    
        # Merge the CLAHE enhanced L-channel with the a and b channel
        limg = cv2.merge((cl,a,b))
    
        # Converting image from LAB Color model to RGB model
        img = cv2.cvtColor(limg, cv2.COLOR_LAB2BGR)
    
        return img
   
    # if image open fail, write log of fail image full path
    def tracelog(self, text):
        if text != "":
            text += "\n"
            f = open("tracelog.log","a") 
            f.write(text)
            f.close()

    
    # filtering processing
    def imgFiltering(self):   
        dirnames = os.listdir(self.origin_folder_path)
        
        for dir in dirnames:
            origin_file_path = self.origin_folder_path + dir
            filter_file_path = self.filter_folder_path

            for path, folder, files in os.walk(origin_file_path):
                for file in files:
                    save_path = filter_file_path + dir
                    os.makedirs(save_path, exist_ok=True)

                    # file check
                    if os.path.isfile(save_path):
                        print("filter file exists : {}".format(save_path))
                        continue
                        
                    input_image = origin_file_path + '/' + file
                    img = cv2.imdecode(np.fromfile(input_image, dtype=np.uint8), cv2.IMREAD_UNCHANGED)

                    # image check
                    if img is None:
                        self.tracelog(origin_file_path +'/'+ file)
                        print('typecheck ', type(img))
                        continue
                    
                    ''' white background '''
                    img = self.white_Background(img)
                    
                    ''' default filter '''
                    img = self.max_con_CLAHE(img)
                    img = self.max_con_CLAHE(img)
                    
                    ''' filtering image save '''
                    ret, img = cv2.imencode('.jpg', img)

                    if ret:
                        with open(save_path + '/' + file[:-4] + "_f.jpg", mode='w+b') as f:
                            img.tofile(f)

                    print(f'Filter : {save_path}' + '/' + f'{file[:-4]}' + '_f.jpg')