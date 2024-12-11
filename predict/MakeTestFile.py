import pandas as pd

data = {
    'shape': ['pentagon'],
    'drug_code': ['none']
}

df = pd.DataFrame(data)

df.to_csv('predict/testcsv.csv', sep='\t', index=True, encoding='utf-8')
